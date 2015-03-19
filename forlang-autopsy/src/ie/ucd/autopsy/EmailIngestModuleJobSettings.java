package ie.ucd.autopsy;

import org.sleuthkit.autopsy.ingest.IngestModuleIngestJobSettings;

/**
 * Ingest job options for email ingest module instances.
 */
public final class EmailIngestModuleJobSettings implements IngestModuleIngestJobSettings {

    private static final long serialVersionUID = 1L;
    private boolean addToGraphDatabase = false;

    public EmailIngestModuleJobSettings() {
    }

    public EmailIngestModuleJobSettings(boolean addToGraphDatabase) {
        this.addToGraphDatabase = addToGraphDatabase;
    }

    /**
     * Get whether email messages should be pumped into the grapgh database during the ingest phase
     *
     * @return boolean Defaults to <code>false</code>
     */
    public final boolean addToGraphDatabase() {
        return addToGraphDatabase;
    }

    @Override
    public final long getVersionNumber() {
        return serialVersionUID;
    }

    /**
     * Set whether email messages should be pumped into the grapgh database during the ingest phase
     *
     * @param boolean addToGraphDatabase
     */
    public final void setAddToGraphDatabase(boolean addToGraphDatabase) {
        this.addToGraphDatabase = addToGraphDatabase;
    }
}
