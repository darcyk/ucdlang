package ie.ucd.autopsy;

import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;
import org.sleuthkit.autopsy.examples.SampleIngestModuleFactory;
import org.sleuthkit.autopsy.ingest.DataSourceIngestModule;
import org.sleuthkit.autopsy.ingest.FileIngestModule;
import org.sleuthkit.autopsy.ingest.IngestModuleFactory;
import org.sleuthkit.autopsy.ingest.IngestModuleGlobalSettingsPanel;
import org.sleuthkit.autopsy.ingest.IngestModuleIngestJobSettings;
import org.sleuthkit.autopsy.ingest.IngestModuleIngestJobSettingsPanel;

@ServiceProvider(service = IngestModuleFactory.class)
public final class EmailIngestModuleFactory implements IngestModuleFactory {

	public static final String VERSION_NUMBER = "1.0.0";

	public static final String getModuleName() {
		return NbBundle.getMessage(EmailIngestModuleFactory.class, "EmailIngestModuleFactory.moduleName");
	}

	/** not supported */
	@Override
	public final DataSourceIngestModule createDataSourceIngestModule(IngestModuleIngestJobSettings settings) {
		throw new UnsupportedOperationException();
	}

	@Override
	public FileIngestModule createFileIngestModule(IngestModuleIngestJobSettings settings) {
		if (!(settings instanceof EmailIngestModuleJobSettings)) {
			throw new IllegalArgumentException(
					"Expected settings argument to be instanceof EmailIngestModuleJobSettings");
		}
		return new EmailFileIngestModule((EmailIngestModuleJobSettings) settings);
	}

	@Override
	public final IngestModuleIngestJobSettings getDefaultIngestJobSettings() {
		return new EmailIngestModuleJobSettings();
	}

	/** Does not support global settings */
	@Override
	public IngestModuleGlobalSettingsPanel getGlobalSettingsPanel() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Create the configuration panel for module initialisation
	 * 
	 * @return IngestModuleIngestJobSettingsPanel
	 */
	@Override
	public final IngestModuleIngestJobSettingsPanel getIngestJobSettingsPanel(IngestModuleIngestJobSettings settings) {
		if (!(settings instanceof EmailIngestModuleJobSettings)) {
			throw new IllegalArgumentException("Expected settings argument to be instanceof EmailIngestModuleJobSettings");
		}
		return new EmailIngestModuleJobSettingsPanel((EmailIngestModuleJobSettings) settings);
	}

	@Override
	public final String getModuleDescription() {
		return NbBundle.getMessage(SampleIngestModuleFactory.class, "EmailIngestModuleFactory.moduleDescription");
	}

	/** Default implementation */
	@Override
	public final String getModuleDisplayName() {
		return getModuleName();
	}

	/**
	 * Version number
	 * 
	 * @return String The version number of the module
	 */
	@Override
	public final String getModuleVersionNumber() {
		return VERSION_NUMBER;
	}

	/**
	 * Does not have a global config panel, but probably should to allow config of location of graph database, type of
	 * graph database etc. Possible future enhancement
	 * 
	 * @return boolean Always <code>false</code>
	 */
	@Override
	public final boolean hasGlobalSettingsPanel() {
		return false;
	}

	/**
	 * Has panel to enable/disable the flushing of email information to a graph database
	 * 
	 * @return boolean Always <code>true</code>
	 */
	@Override
	public final boolean hasIngestJobSettingsPanel() {
		return true;
	}

	/**
	 * Does not read data sources
	 * 
	 * @return boolean Always <code>false</code>
	 */
	@Override
	public final boolean isDataSourceIngestModuleFactory() {
		return false;
	}

	/**
	 * Reads in individual PST and MBOX files to parse out email messages
	 * 
	 * @return boolean Always <code>true</code>
	 */
	@Override
	public final boolean isFileIngestModuleFactory() {
		return true;
	}
}