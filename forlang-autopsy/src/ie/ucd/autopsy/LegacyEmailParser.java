package ie.ucd.autopsy;

import ie.ucd.forlang.neo4j.GraphDatabaseUtils;
import ie.ucd.forlang.neo4j.object.EmailAccount;
import ie.ucd.forlang.neo4j.object.EmailAccountImpl;
import ie.ucd.forlang.neo4j.object.EmailMessageImpl;
import java.io.File;
import java.io.IOException;
import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import org.neo4j.graphdb.GraphDatabaseService;
import org.sleuthkit.autopsy.casemodule.Case;
import org.sleuthkit.autopsy.casemodule.services.FileManager;
import org.sleuthkit.autopsy.coreutils.Logger;
import org.sleuthkit.autopsy.datamodel.ContentUtils;
import org.sleuthkit.autopsy.ingest.IngestJobContext;
import org.sleuthkit.autopsy.ingest.IngestMessage;
import org.sleuthkit.autopsy.ingest.IngestModule.ProcessResult;
import org.sleuthkit.autopsy.ingest.IngestServices;
import org.sleuthkit.autopsy.ingest.ModuleContentEvent;
import org.sleuthkit.autopsy.ingest.ModuleDataEvent;
import org.sleuthkit.datamodel.AbstractFile;
import org.sleuthkit.datamodel.BlackboardArtifact;
import org.sleuthkit.datamodel.BlackboardAttribute;
import org.sleuthkit.datamodel.BlackboardAttribute.ATTRIBUTE_TYPE;
import org.sleuthkit.datamodel.DerivedFile;
import org.sleuthkit.datamodel.TskCoreException;
import org.sleuthkit.datamodel.TskData;
import org.sleuthkit.datamodel.TskException;

/**
 * File-level ingest module that detects MBOX files based on signature. Understands Thunderbird folder layout to provide
 * additional structure and metadata.
 */
public final class LegacyEmailParser {

    private static final Logger logger = Logger.getLogger(LegacyEmailParser.class.getName());
    private IngestServices services = IngestServices.getInstance();
    private FileManager fileManager = Case.getCurrentCase().getServices().getFileManager();
    ;
    private IngestJobContext context = null;
    private GraphDatabaseService graphDb = null;
    private boolean addToGrapgDb = false;

    public LegacyEmailParser() {
        super();
    }

    public ProcessResult process(IngestJobContext context, AbstractFile abstractFile, GraphDatabaseService graphDb, boolean addToGrapgDb) {
        this.context = context;
        this.graphDb = graphDb;
        this.addToGrapgDb = addToGrapgDb;
        // skip known
        if (abstractFile.getKnown().equals(TskData.FileKnown.KNOWN)) {
            return ProcessResult.OK;
        }

        //skip unalloc
        if (abstractFile.getType().equals(TskData.TSK_DB_FILES_TYPE_ENUM.UNALLOC_BLOCKS)) {
            return ProcessResult.OK;
        }

        if (abstractFile.isVirtual()) {
            return ProcessResult.OK;
        }

        // check its signature
        boolean isMbox = false;
        try {
            byte[] t = new byte[64];
            if (abstractFile.getSize() > 64) {
                int byteRead = abstractFile.read(t, 0, 64);
                if (byteRead > 0) {
                    isMbox = MboxParser.isValidMimeTypeMbox(t);
                }
            }
        }
        catch (TskException ex) {
            logger.log(Level.WARNING, null, ex);
        }

        if (isMbox) {
            return processMBox(abstractFile);
        }

        if (PstParser.isPstFile(abstractFile)) {
            return processPst(abstractFile);
        }

        return ProcessResult.OK;
    }

    /**
     * Processes a pst/ost data file and extracts and adds email artifacts.
     *
     * @param abstractFile The pst/ost data file to process.
     * @return
     */
    private ProcessResult processPst(AbstractFile abstractFile) {
        String fileName = getTempPath() + File.separator + abstractFile.getName()
                + "-" + String.valueOf(abstractFile.getId());
        File file = new File(fileName);

        if (abstractFile.getSize() >= services.getFreeDiskSpace()) {
            logger.log(Level.WARNING, "Not enough disk space to write file to disk."); //NON-NLS
            IngestMessage msg = IngestMessage.createErrorMessage(EmailIngestModuleFactory.getModuleName(), EmailIngestModuleFactory.getModuleName(), "processPst.errMsg.outOfDiskSpace");
            services.postMessage(msg);
            return ProcessResult.OK;
        }

        try {
            ContentUtils.writeToFile(abstractFile, file);
        }
        catch (IOException ex) {
            logger.log(Level.WARNING, "Failed writing pst file to disk.", ex); //NON-NLS
            return ProcessResult.OK;
        }

        PstParser parser = new PstParser(services);
        PstParser.ParseResult result = parser.parse(file);

        if (result == PstParser.ParseResult.OK) {
            // parse success: Process email and add artifacts
            processEmails(parser.getResults(), abstractFile);
        }
        else if (result == PstParser.ParseResult.ENCRYPT) {
            // encrypted pst: Add encrypted file artifact
            try {
                BlackboardArtifact generalInfo = abstractFile.getGenInfoArtifact();
                generalInfo.addAttribute(new BlackboardAttribute(ATTRIBUTE_TYPE.TSK_ENCRYPTION_DETECTED.getTypeID(),
                        EmailIngestModuleFactory.getModuleName(),
                        "File-level Encryption"));
            }
            catch (TskCoreException ex) {
                logger.log(Level.INFO, "Failed to add encryption attribute to file: {0}", abstractFile.getName()); //NON-NLS
            }
        }
        else {
            // parsing error: log message
            postErrorMessage("Error while processing " + abstractFile.getName(), "Only files from Outlook 2003 and later are supported.");
            logger.log(Level.INFO, "PSTParser failed to parse {0}", abstractFile.getName()); //NON-NLS
            return ProcessResult.ERROR;
        }

        if (file.delete() == false) {
            logger.log(Level.INFO, "Failed to delete temp file: {0}", file.getName()); //NON-NLS
        }

        String errors = parser.getErrors();
        if (errors.isEmpty() == false) {
            postErrorMessage("Error while processing " + abstractFile.getName(), errors);
        }

        return ProcessResult.OK;
    }

    /**
     * Parse and extract email messages and attachments from an MBox file.
     *
     * @param abstractFile
     * @param ingestContext
     * @return
     */
    private ProcessResult processMBox(AbstractFile abstractFile) {
        String mboxFileName = abstractFile.getName();
        String mboxParentDir = abstractFile.getParentPath();
        // use the local path to determine the e-mail folder structure
        String emailFolder = "";
        // email folder is everything after "Mail" or ImapMail
        if (mboxParentDir.contains("/Mail/")) { //NON-NLS
            emailFolder = mboxParentDir.substring(mboxParentDir.indexOf("/Mail/") + 5); //NON-NLS
        }
        else if (mboxParentDir.contains("/ImapMail/")) { //NON-NLS
            emailFolder = mboxParentDir.substring(mboxParentDir.indexOf("/ImapMail/") + 9); //NON-NLS
        }
        emailFolder = emailFolder + mboxFileName;
        emailFolder = emailFolder.replaceAll(".sbd", ""); //NON-NLS

        String fileName = getTempPath() + File.separator + abstractFile.getName()
                + "-" + String.valueOf(abstractFile.getId());
        File file = new File(fileName);

        if (abstractFile.getSize() >= services.getFreeDiskSpace()) {
            logger.log(Level.WARNING, "Not enough disk space to write file to disk."); //NON-NLS
            postErrorMessage("Error while processing " + abstractFile.getName(), "Out of disk space. Cannot copy file to parse.");
            return ProcessResult.OK;
        }

        try {
            ContentUtils.writeToFile(abstractFile, file);
        }
        catch (IOException ex) {
            logger.log(Level.WARNING, "Failed writing mbox file to disk.", ex); //NON-NLS
            return ProcessResult.OK;
        }

        MboxParser parser = new MboxParser(services, emailFolder);
        List<EmailMessage> emails = parser.parse(file);

        processEmails(emails, abstractFile);

        if (file.delete() == false) {
            logger.log(Level.INFO, "Failed to delete temp file: {0}", file.getName()); //NON-NLS
        }

        String errors = parser.getErrors();
        if (errors.isEmpty() == false) {
            postErrorMessage("Error while processing " + abstractFile.getName(), errors);
        }

        return ProcessResult.OK;
    }

    /**
     * Get a path to a temporary folder.
     *
     * @return
     */
    public static String getTempPath() {
        String tmpDir = Case.getCurrentCase().getTempDirectory() + File.separator
                + "EmailParser"; //NON-NLS
        File dir = new File(tmpDir);
        if (dir.exists() == false) {
            dir.mkdirs();
        }
        return tmpDir;
    }

    public static String getModuleOutputPath() {
        String outDir = Case.getCurrentCase().getModulesOutputDirAbsPath() + File.separator
                + EmailIngestModuleFactory.getModuleName();
        File dir = new File(outDir);
        if (dir.exists() == false) {
            dir.mkdirs();
        }
        return outDir;
    }

    public static String getRelModuleOutputPath() {
        return Case.getModulesOutputDirRelPath() + File.separator
                + EmailIngestModuleFactory.getModuleName();
    }

    /**
     * Take the extracted information in the email messages and add the appropriate artifacts and derived files.
     *
     * @param emails
     * @param abstractFile
     * @param ingestContext
     */
    private void processEmails(List<EmailMessage> emails, AbstractFile abstractFile) {
        List<AbstractFile> derivedFiles = new ArrayList<>();
        for (EmailMessage email : emails) {
            if (email.hasAttachment()) {
                derivedFiles.addAll(handleAttachments(email.getAttachments(), abstractFile));
            }
            addArtifact(email, abstractFile);
        }

        if (derivedFiles.isEmpty() == false) {
            for (AbstractFile derived : derivedFiles) {
                services.fireModuleContentEvent(new ModuleContentEvent(derived));
            }
        }
        context.scheduleFiles(derivedFiles);//addFilesToJob(derivedFiles);
        services.fireModuleDataEvent(new ModuleDataEvent(EmailIngestModuleFactory.getModuleName(), BlackboardArtifact.ARTIFACT_TYPE.TSK_EMAIL_MSG));
    }

    /**
     * Add the given attachments as derived files and reschedule them for ingest.
     *
     * @param attachments
     * @param abstractFile
     * @return
     */
    private List<AbstractFile> handleAttachments(List<EmailMessage.Attachment> attachments, AbstractFile abstractFile) {
        List<AbstractFile> files = new ArrayList<>();
        for (EmailMessage.Attachment attach : attachments) {
            String filename = attach.getName();
            long crTime = attach.getCrTime();
            long mTime = attach.getmTime();
            long aTime = attach.getaTime();
            long cTime = attach.getcTime();
            String relPath = attach.getLocalPath();
            long size = attach.getSize();

            try {
                DerivedFile df = fileManager.addDerivedFile(filename, relPath,
                        size, cTime, crTime, aTime, mTime, true, abstractFile, "",
                        EmailIngestModuleFactory.getModuleName(), new EmailIngestModuleFactory().getModuleVersionNumber(), "");
                files.add(df);
            }
            catch (TskCoreException ex) {
                postErrorMessage("Error processing " + abstractFile.getName(), "Failed to add attachment named " + filename + " to the case.");
                logger.log(Level.INFO, "", ex);
            }
        }
        return files;
    }

    /**
     * Add a blackboard artifact for the given email message.
     *
     * @param email
     * @param abstractFile
     */
    private void addArtifact(EmailMessage email, AbstractFile abstractFile) {
        List<BlackboardAttribute> bbattributes = new ArrayList<>();
        String to = email.getRecipients();
        String cc = email.getCc();
        String bcc = email.getBcc();
        String from = email.getSender();
        long dateL = email.getSentDate();
        String body = email.getTextBody();
        String bodyHTML = email.getHtmlBody();
        String rtf = email.getRtfBody();
        String subject = email.getSubject();
        long id = email.getId();
        String localPath = email.getLocalPath();

        try {
            if (addToGrapgDb) {
                String[] addrs = null;
                EmailAccount fromAcc = null;
                List<EmailAccount> recipients = new ArrayList<>();
                EmailMessageImpl message = null;
                if (!from.isEmpty()) {
                    fromAcc = new EmailAccountImpl(cleanEmailAddress(from));
                }
                if (!to.isEmpty()) {
                    addrs = to.split("; ");
                    for (String addr : addrs) {
                        recipients.add(new EmailAccountImpl(cleanEmailAddress(addr)));
                    }
                }
                if (!cc.isEmpty()) {
                    addrs = cc.split("; ");
                    for (String addr : addrs) {
                        recipients.add(new EmailAccountImpl(cleanEmailAddress(addr)));
                    }
                }
                if (!bcc.isEmpty()) {
                    addrs = bcc.split("; ");
                    for (String addr : addrs) {
                        recipients.add(new EmailAccountImpl(cleanEmailAddress(addr)));
                    }
                }
                if (!subject.isEmpty()) {
                    message = new EmailMessageImpl(new UID().toString(), from, to.split("; "), subject, new Date(dateL));
                }
                GraphDatabaseUtils.linkEmailChain(graphDb, message, fromAcc, recipients);
            }
        }
        catch (Exception e) {
            logger.log(Level.WARNING, "could not add entry to graph database", e);
        }

        if (to.isEmpty() == false) {
            bbattributes.add(new BlackboardAttribute(ATTRIBUTE_TYPE.TSK_EMAIL_TO.getTypeID(), EmailIngestModuleFactory.getModuleName(), to));
        }
        if (cc.isEmpty() == false) {
            bbattributes.add(new BlackboardAttribute(ATTRIBUTE_TYPE.TSK_EMAIL_CC.getTypeID(), EmailIngestModuleFactory.getModuleName(), cc));
        }
        if (bcc.isEmpty() == false) {
            bbattributes.add(new BlackboardAttribute(ATTRIBUTE_TYPE.TSK_EMAIL_BCC.getTypeID(), EmailIngestModuleFactory.getModuleName(), bcc));
        }
        if (from.isEmpty() == false) {
            bbattributes.add(new BlackboardAttribute(ATTRIBUTE_TYPE.TSK_EMAIL_FROM.getTypeID(), EmailIngestModuleFactory.getModuleName(), from));
        }
        if (dateL > 0) {
            bbattributes.add(new BlackboardAttribute(ATTRIBUTE_TYPE.TSK_DATETIME_RCVD.getTypeID(), EmailIngestModuleFactory.getModuleName(), dateL));
            bbattributes.add(new BlackboardAttribute(ATTRIBUTE_TYPE.TSK_DATETIME_SENT.getTypeID(), EmailIngestModuleFactory.getModuleName(), dateL));
        }
        if (body.isEmpty() == false) {
            bbattributes.add(new BlackboardAttribute(ATTRIBUTE_TYPE.TSK_EMAIL_CONTENT_PLAIN.getTypeID(), EmailIngestModuleFactory.getModuleName(), body));
        }
        if (bodyHTML.isEmpty() == false) {
            bbattributes.add(new BlackboardAttribute(ATTRIBUTE_TYPE.TSK_EMAIL_CONTENT_HTML.getTypeID(), EmailIngestModuleFactory.getModuleName(), bodyHTML));
        }
        if (rtf.isEmpty() == false) {
            bbattributes.add(new BlackboardAttribute(ATTRIBUTE_TYPE.TSK_EMAIL_CONTENT_RTF.getTypeID(), EmailIngestModuleFactory.getModuleName(), rtf));
        }
        bbattributes.add(new BlackboardAttribute(ATTRIBUTE_TYPE.TSK_MSG_ID.getTypeID(), EmailIngestModuleFactory.getModuleName(), ((id < 0L) ? "Not available" : String.valueOf(id))));
        if (subject.isEmpty() == false) {
            bbattributes.add(new BlackboardAttribute(ATTRIBUTE_TYPE.TSK_SUBJECT.getTypeID(), EmailIngestModuleFactory.getModuleName(), subject));
        }
        if (localPath.isEmpty() == false) {
            bbattributes.add(new BlackboardAttribute(ATTRIBUTE_TYPE.TSK_PATH.getTypeID(), EmailIngestModuleFactory.getModuleName(), localPath));
        }
        else {
            bbattributes.add(new BlackboardAttribute(ATTRIBUTE_TYPE.TSK_PATH.getTypeID(), EmailIngestModuleFactory.getModuleName(), "/foo/bar")); //NON-NLS
        }

        try {
            BlackboardArtifact bbart;
            bbart = abstractFile.newArtifact(BlackboardArtifact.ARTIFACT_TYPE.TSK_EMAIL_MSG);
            bbart.addAttributes(bbattributes);
        }
        catch (TskCoreException ex) {
            logger.log(Level.WARNING, null, ex);
        }
    }

    private String cleanEmailAddress(String email) {
        String clean = null;
        if (email != null) {
            clean = email.trim();
            if (clean.endsWith(";")) {
                clean = clean.replace(";", "");
            }
        }
        return clean;
    }

    void postErrorMessage(String subj, String details) {
        IngestMessage ingestMessage = IngestMessage.createErrorMessage(new EmailIngestModuleFactory().getModuleVersionNumber(), subj, details);
        services.postMessage(ingestMessage);
    }

    private IngestServices getServices() {
        return services;
    }
}
