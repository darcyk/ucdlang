package ie.ucd.autopsy;

import ie.ucd.forlang.neo4j.GraphManager;
import java.util.logging.Level;

import org.sleuthkit.autopsy.casemodule.Case;
import org.sleuthkit.autopsy.casemodule.services.FileManager;
import org.sleuthkit.autopsy.coreutils.Logger;
import org.sleuthkit.autopsy.ingest.FileIngestModule;
import org.sleuthkit.autopsy.ingest.IngestJobContext;
import org.sleuthkit.autopsy.ingest.IngestModule;
import org.sleuthkit.datamodel.AbstractFile;
import org.sleuthkit.datamodel.TskData;

/**
 * Read in a file and determine if it's a supported email mailbox format. If so, create the relevant parser to extract
 * individual email objects
 */
public final class EmailFileIngestModule implements FileIngestModule {

    public static final String MIME_TYPE_OUTLOOK = "application/vnd.ms-outlook-pst";
    public static final String MIME_TYPE_RFC822 = "message/rfc822";
    /**
     * Class logger
     */
    private static final Logger LOG = Logger.getLogger(EmailFileIngestModule.class.getName());
    /**
     * TODO
     */
    private IngestJobContext context = null;
    /**
     * File manager to add attachments back to the ingest list for scanning
     */
    private FileManager fileManager = null;
    /**
     * Job settings
     */
    private EmailIngestModuleJobSettings settings = null;

    private GraphManager graphManager = null;

    public EmailFileIngestModule(EmailIngestModuleJobSettings settings) {
        this.settings = settings;
    }

    /**
     * If the file is a valid and supported email archive type, create a parser and parse it. Otherwise do nothing.
     */
    @Override
    public final IngestModule.ProcessResult process(AbstractFile file) {
        LOG.log(Level.INFO, "starting processing...");
        // Skip anything other than actual file system files.
        if (TskData.TSK_DB_FILES_TYPE_ENUM.UNALLOC_BLOCKS.equals(file.getType())
                || TskData.TSK_DB_FILES_TYPE_ENUM.UNUSED_BLOCKS.equals(file.getType())) {
            return IngestModule.ProcessResult.OK;
        }
        // Skip known files.
        if (TskData.FileKnown.KNOWN.equals(file.getKnown())) {
            return IngestModule.ProcessResult.OK;
        }
        if (file.isVirtual()) {
            return ProcessResult.OK;
        }
        //return ProcessResult.OK;
//        String mimeType = null;

        try {
            // determine if its a supported file type
            return new LegacyEmailParser().process(context, file, graphManager, settings.addToGraphDatabase());
//            mimeType = new Tika().detect(new File(Case.getCurrentCase().getTempDirectory(), file.getName()));
//            if (MIME_TYPE_OUTLOOK.equals(mimeType)) {
//                LOG.log(Level.INFO, "outlook file detected: " + file.getId() + ", " + file.getName());
//            }
//            else if (MIME_TYPE_RFC822.equals(mimeType)) {
//                LOG.log(Level.INFO, "rfc822 file detected: " + file.getId() + ", " + file.getName());
//            }
//            return IngestModule.ProcessResult.OK;
        }
        catch (Exception e) {
            LOG.log(Level.SEVERE, "could not process file (id = " + file.getId() + ")", e);
            return IngestModule.ProcessResult.ERROR;
        }
        finally {
            // be a good citizen
            //           mimeType = null;
        }
    }

    /**
     * Does nothing
     */
    @Override
    public final void shutDown() {
    }

    /**
     * Store the context object and get access to the current <code>FileManager</code> object
     *
     * @see FileManager
     */
    @Override
    public final void startUp(IngestJobContext context) throws IngestModuleException {
        try {
            this.context = context;
            fileManager = Case.getCurrentCase().getServices().getFileManager();
        }
        catch (Exception e) {
            LOG.log(Level.SEVERE, "start up failed", e);
            throw new IngestModuleException("could not start EmailFileIngestModule: " + e.toString());
        }
    }
}
