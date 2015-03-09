package ie.ucd.autopsy;

import java.io.File;
import java.util.logging.Level;

import org.apache.tika.config.TikaConfig;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
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

	/** Class logger */
	private static final Logger LOG = Logger.getLogger(EmailFileIngestModule.class.getName());
	/** TODO */
	private IngestJobContext context = null;
	/** File manager to add attachments back to the ingest list for scanning */
	private FileManager fileManager = null;
	/** Job settings */
	private EmailIngestModuleJobSettings settings = null;

	public EmailFileIngestModule(EmailIngestModuleJobSettings settings) {
		this.settings = settings;
	}

	/**
	 * If the file is a valid and supported email archive type, create a parser and parse it. Otherwise do nothing.
	 */
	@Override
	public final IngestModule.ProcessResult process(AbstractFile file) {
		LOG.log(Level.INFO, "Sarting processing...");
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
		TikaConfig tika = null;
		Metadata metadata = null;
		MediaType type = null;
		try {
			// determine if its a supported file type
			tika = new TikaConfig();
			metadata = new Metadata();
			metadata.set(Metadata.RESOURCE_NAME_KEY, file.getName());
			type = tika.getDetector().detect(
					TikaInputStream.get(new File(Case.getCurrentCase().getTempDirectory(), file.getName())), metadata);
			LOG.log(Level.INFO, "Detected type: " + type.toString());
			LOG.log(Level.INFO, "Processing complete");
			return IngestModule.ProcessResult.OK;
		}
		catch (Exception e) {
			LOG.log(Level.SEVERE, "Could not process file (id = " + file.getId() + ")", e);
			return IngestModule.ProcessResult.ERROR;
		}
		finally {
			// be a good citizen
			tika = null;
			metadata = null;
			type = null;
		}
	}

	/** Does nothing */
	@Override
	public final void shutDown() {
		LOG.log(Level.INFO, "Shutting down...");
		LOG.log(Level.INFO, "Shutdown complete");
	}

	/**
	 * Store the context object and get access to the current <code>FileManager</code> object
	 * 
	 * @see FileManager
	 */
	@Override
	public final void startUp(IngestJobContext context) throws IngestModuleException {
		try {
			LOG.log(Level.INFO, "Starting up...");
			this.context = context;
			fileManager = Case.getCurrentCase().getServices().getFileManager();
			LOG.log(Level.INFO, "Start up complete");
		}
		catch (Exception e) {
			LOG.log(Level.SEVERE, "Start up failed", e);
			throw new IngestModuleException("Could not start EmailFileIngestModule: " + e.toString());
		}
	}
}
