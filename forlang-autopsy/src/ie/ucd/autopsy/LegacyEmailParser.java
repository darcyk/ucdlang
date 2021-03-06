package ie.ucd.autopsy;

import ie.ucd.forlang.neo4j.GraphDatabaseUtils;
import ie.ucd.forlang.neo4j.object.EmailAccount;
import ie.ucd.forlang.neo4j.object.EmailAccountImpl;
import ie.ucd.forlang.neo4j.object.EmailMessageImpl;
import ie.ucd.forlang.neo4j.object.PersonImpl;

import java.io.File;
import java.io.IOException;
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
	private boolean addToGrapgDb = false;
	private IngestJobContext context = null;;
	private FileManager fileManager = Case.getCurrentCase().getServices().getFileManager();
	private GraphDatabaseService graphDb = null;
	private IngestServices services = IngestServices.getInstance();

	public LegacyEmailParser() {
		super();
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
		return Case.getModulesOutputDirRelPath() + File.separator + EmailIngestModuleFactory.getModuleName();
	}

	/**
	 * Get a path to a temporary folder.
	 *
	 * @return
	 */
	public static String getTempPath() {
		String tmpDir = Case.getCurrentCase().getTempDirectory() + File.separator + "EmailParser"; // NON-NLS
		File dir = new File(tmpDir);
		if (dir.exists() == false) {
			dir.mkdirs();
		}
		return tmpDir;
	}

	public IngestServices getServices() {
		return services;
	}

	public ProcessResult process(IngestJobContext context, AbstractFile abstractFile, GraphDatabaseService graphDb,
			boolean addToGrapgDb) {
		this.context = context;
		this.graphDb = graphDb;
		this.addToGrapgDb = addToGrapgDb;
		// skip known
		if (abstractFile.getKnown().equals(TskData.FileKnown.KNOWN)) {
			return ProcessResult.OK;
		}
		// skip unalloc
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
	 * Add a blackboard artifact for the given email message.
	 *
	 * @param email
	 * @param abstractFile
	 */
	private void addArtifact(EmailMessage email, AbstractFile abstractFile) {
		List<BlackboardAttribute> bbattributes = new ArrayList<>();
		// neo4j extension
		try {
			if (addToGrapgDb) {
				EmailAccount from = null;
				List<EmailAccount> recipList = new ArrayList<>();
				List<String> recipStrs = new ArrayList<>();
				EmailMessageImpl message = null;
				if (email.getFrom() != null) {
					createOwnerLink(email.getFrom());
					from = new EmailAccountImpl(email.getFrom().getAddress());
				}
				if (email.getTos().size() > 0) {
					for (EmailAddress addr : email.getTos()) {
						createOwnerLink(addr);
						recipList.add(new EmailAccountImpl(addr.getAddress()));
						recipStrs.add(addr.getAddress());
					}
				}
				if (email.getCcs().size() > 0) {
					for (EmailAddress addr : email.getCcs()) {
						createOwnerLink(addr);
						recipList.add(new EmailAccountImpl(addr.getAddress()));
						recipStrs.add(addr.getAddress());
					}
				}
				if (email.getBccs().size() > 0) {
					for (EmailAddress addr : email.getBccs()) {
						createOwnerLink(addr);
						recipList.add(new EmailAccountImpl(addr.getAddress()));
						recipStrs.add(addr.getAddress());
					}
				}
				logger.log(Level.INFO, "attempting to add email aritifact to database: " + email.getHashCode() + ","
						+ from.getEmailAddress() + "," + recipStrs.toString() + "," + email.getSubject()
						+ "," + email.getSentDate());
				message = new EmailMessageImpl(email.getHashCode(), from.getEmailAddress(),
						recipStrs.toArray(new String[0]), email.getSubject(), new Date(email.getSentDate()));
				GraphDatabaseUtils.linkEmailChain(graphDb, message, from, recipList);
			}
		}
		catch (Throwable e) {
			logger.log(Level.WARNING, "could not add entry to graph database", e);
		}
		if (email.getFrom() != null) {
			bbattributes.add(new BlackboardAttribute(ATTRIBUTE_TYPE.TSK_EMAIL_FROM.getTypeID(),
					EmailIngestModuleFactory.getModuleName(), email.getFrom().toString()));
		}
		if (email.getTos().size() > 0) {
			bbattributes.add(new BlackboardAttribute(ATTRIBUTE_TYPE.TSK_EMAIL_TO.getTypeID(), EmailIngestModuleFactory
					.getModuleName(), email.getTos().toString()));
		}
		if (email.getCcs().size() > 0) {
			bbattributes.add(new BlackboardAttribute(ATTRIBUTE_TYPE.TSK_EMAIL_CC.getTypeID(), EmailIngestModuleFactory
					.getModuleName(), email.getCcs().toString()));
		}
		if (email.getBccs().size() > 0) {
			bbattributes.add(new BlackboardAttribute(ATTRIBUTE_TYPE.TSK_EMAIL_BCC.getTypeID(), EmailIngestModuleFactory
					.getModuleName(), email.getBccs().toString()));
		}
		if (email.getSentDate() != 0) {
			bbattributes.add(new BlackboardAttribute(ATTRIBUTE_TYPE.TSK_DATETIME_RCVD.getTypeID(),
					EmailIngestModuleFactory.getModuleName(), email.getSentDate()));
			bbattributes.add(new BlackboardAttribute(ATTRIBUTE_TYPE.TSK_DATETIME_SENT.getTypeID(),
					EmailIngestModuleFactory.getModuleName(), email.getSentDate()));
		}
		if (email.getTextBody() != null) {
			bbattributes.add(new BlackboardAttribute(ATTRIBUTE_TYPE.TSK_EMAIL_CONTENT_PLAIN.getTypeID(),
					EmailIngestModuleFactory.getModuleName(), email.getTextBody()));
		}
		if (email.getHtmlBody() != null) {
			bbattributes.add(new BlackboardAttribute(ATTRIBUTE_TYPE.TSK_EMAIL_CONTENT_HTML.getTypeID(),
					EmailIngestModuleFactory.getModuleName(), email.getHtmlBody()));
		}
		if (email.getRtfBody() != null) {
			bbattributes.add(new BlackboardAttribute(ATTRIBUTE_TYPE.TSK_EMAIL_CONTENT_RTF.getTypeID(),
					EmailIngestModuleFactory.getModuleName(), email.getRtfBody()));
		}
		bbattributes.add(new BlackboardAttribute(ATTRIBUTE_TYPE.TSK_MSG_ID.getTypeID(), EmailIngestModuleFactory
				.getModuleName(), ((email.getId() == 0) ? "Not available" : String.valueOf(email.getId()))));
		if (email.getSubject() != null) {
			bbattributes.add(new BlackboardAttribute(ATTRIBUTE_TYPE.TSK_SUBJECT.getTypeID(), EmailIngestModuleFactory
					.getModuleName(), email.getSubject()));
		}
		if (email.getLocalPath() != null) {
			bbattributes.add(new BlackboardAttribute(ATTRIBUTE_TYPE.TSK_PATH.getTypeID(), EmailIngestModuleFactory
					.getModuleName(), email.getLocalPath()));
		}
		else {
			bbattributes.add(new BlackboardAttribute(ATTRIBUTE_TYPE.TSK_PATH.getTypeID(), EmailIngestModuleFactory
					.getModuleName(), "/foo/bar")); // NON-NLS
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

	private final void createOwnerLink(EmailAddress addr) {
		if (addr != null && addr.getAddress() != null && !addr.getAddress().isEmpty() && addr.getName() != null
				&& !addr.getName().isEmpty()) {
			GraphDatabaseUtils.linkPersonToEmailAccount(graphDb, new PersonImpl(addr.getName()), new EmailAccountImpl(
					addr.getAddress()));
		}
	}

	/**
	 * Add the given attachments as derived files and reschedule them for ingest.
	 *
	 * @param attachments
	 * @param abstractFile
	 * @return
	 */
	private List<AbstractFile> handleAttachments(List<EmailAttachment> attachments, AbstractFile abstractFile) {
		List<AbstractFile> files = new ArrayList<>();
		for (EmailAttachment attach : attachments) {
			String filename = attach.getName();
			long crTime = attach.getCrTime();
			long mTime = attach.getmTime();
			long aTime = attach.getaTime();
			long cTime = attach.getcTime();
			String relPath = attach.getLocalPath();
			long size = attach.getSize();
			try {
				DerivedFile df = fileManager.addDerivedFile(filename, relPath, size, cTime, crTime, aTime, mTime, true,
						abstractFile, "", EmailIngestModuleFactory.getModuleName(),
						new EmailIngestModuleFactory().getModuleVersionNumber(), "");
				files.add(df);
			}
			catch (TskCoreException ex) {
				postErrorMessage("Error processing " + abstractFile.getName(), "Failed to add attachment named "
						+ filename + " to the case.");
				logger.log(Level.INFO, "", ex);
			}
		}
		return files;
	}

	private void postErrorMessage(String subj, String details) {
		IngestMessage ingestMessage = IngestMessage.createErrorMessage(
				new EmailIngestModuleFactory().getModuleVersionNumber(), subj, details);
		services.postMessage(ingestMessage);
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
		context.scheduleFiles(derivedFiles);// addFilesToJob(derivedFiles);
		services.fireModuleDataEvent(new ModuleDataEvent(EmailIngestModuleFactory.getModuleName(),
				BlackboardArtifact.ARTIFACT_TYPE.TSK_EMAIL_MSG));
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
		if (mboxParentDir.contains("/Mail/")) { // NON-NLS
			emailFolder = mboxParentDir.substring(mboxParentDir.indexOf("/Mail/") + 5); // NON-NLS
		}
		else if (mboxParentDir.contains("/ImapMail/")) { // NON-NLS
			emailFolder = mboxParentDir.substring(mboxParentDir.indexOf("/ImapMail/") + 9); // NON-NLS
		}
		emailFolder = emailFolder + mboxFileName;
		emailFolder = emailFolder.replaceAll(".sbd", ""); // NON-NLS
		String fileName = getTempPath() + File.separator + abstractFile.getName() + "-"
				+ String.valueOf(abstractFile.getId());
		File file = new File(fileName);
		if (abstractFile.getSize() >= services.getFreeDiskSpace()) {
			logger.log(Level.WARNING, "Not enough disk space to write file to disk."); // NON-NLS
			postErrorMessage("Error while processing " + abstractFile.getName(),
					"Out of disk space. Cannot copy file to parse.");
			return ProcessResult.OK;
		}
		try {
			ContentUtils.writeToFile(abstractFile, file);
		}
		catch (IOException ex) {
			logger.log(Level.WARNING, "Failed writing mbox file to disk.", ex); // NON-NLS
			return ProcessResult.OK;
		}
		MboxParser parser = new MboxParser(emailFolder);
		List<EmailMessage> emails = parser.parse(file);
		processEmails(emails, abstractFile);
		if (file.delete() == false) {
			logger.log(Level.INFO, "Failed to delete temp file: {0}", file.getName()); // NON-NLS
		}
		String errors = parser.getErrors();
		if (errors.isEmpty() == false) {
			postErrorMessage("Error while processing " + abstractFile.getName(), errors);
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
		String fileName = getTempPath() + File.separator + abstractFile.getName() + "-"
				+ String.valueOf(abstractFile.getId());
		File file = new File(fileName);
		if (abstractFile.getSize() >= services.getFreeDiskSpace()) {
			logger.log(Level.WARNING, "Not enough disk space to write file to disk."); // NON-NLS
			IngestMessage msg = IngestMessage.createErrorMessage(EmailIngestModuleFactory.getModuleName(),
					EmailIngestModuleFactory.getModuleName(), "processPst.errMsg.outOfDiskSpace");
			services.postMessage(msg);
			return ProcessResult.OK;
		}
		try {
			ContentUtils.writeToFile(abstractFile, file);
		}
		catch (IOException ex) {
			logger.log(Level.WARNING, "Failed writing pst file to disk.", ex); // NON-NLS
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
						EmailIngestModuleFactory.getModuleName(), "File-level Encryption"));
			}
			catch (TskCoreException ex) {
				logger.log(Level.INFO, "Failed to add encryption attribute to file: {0}", abstractFile.getName()); // NON-NLS
			}
		}
		else {
			// parsing error: log message
			postErrorMessage("Error while processing " + abstractFile.getName(),
					"Only files from Outlook 2003 and later are supported.");
			logger.log(Level.INFO, "PSTParser failed to parse {0}", abstractFile.getName()); // NON-NLS
			return ProcessResult.ERROR;
		}
		if (file.delete() == false) {
			logger.log(Level.INFO, "Failed to delete temp file: {0}", file.getName()); // NON-NLS
		}
		String errors = parser.getErrors();
		if (errors.isEmpty() == false) {
			postErrorMessage("Error while processing " + abstractFile.getName(), errors);
		}
		return ProcessResult.OK;
	}
}
