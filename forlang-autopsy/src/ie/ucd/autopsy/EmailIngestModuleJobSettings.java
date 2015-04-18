package ie.ucd.autopsy;

import java.net.URI;

import org.apache.commons.lang3.Validate;
import org.sleuthkit.autopsy.ingest.IngestModuleIngestJobSettings;

/** Ingest job options for email ingest module instances */
public final class EmailIngestModuleJobSettings implements IngestModuleIngestJobSettings {

	private static final long serialVersionUID = 1L;
	/** Whether email messages should be pumped into the graph database during the ingest phase */
	private boolean addToGraphDatabase = false;
	/** Neo4j password */
	private String neo4jPassword = "";
	/** Neo4j server URI */
	private URI neo4jUri = null;
	/** Neo4j username */
	private String neo4jUsername = "";

	public EmailIngestModuleJobSettings() {
		super();
	}

	public EmailIngestModuleJobSettings(boolean addToGraphDatabase, String neo4jUri, String neo4jUsername,
			String neo4jPassword) {
		super();
		setAddToGraphDatabase(addToGraphDatabase);
		setNeo4jUri(neo4jUri);
		setNeo4jUsername(neo4jUsername);
		setNeo4jPassword(neo4jPassword);
	}

	public final boolean addToGraphDatabase() {
		return addToGraphDatabase;
	}

	public final String getNeo4jPassword() {
		return neo4jPassword;
	}

	public final URI getNeo4jUri() {
		return neo4jUri;
	}

	public final String getNeo4jUriStr() {
		return (neo4jUri == null) ? "" : neo4jUri.toString();
	}

	public final String getNeo4jUsername() {
		return neo4jUsername;
	}

	@Override
	public final long getVersionNumber() {
		return serialVersionUID;
	}

	public final void setAddToGraphDatabase(boolean addToGraphDatabase) {
		this.addToGraphDatabase = addToGraphDatabase;
	}

	public final void setNeo4jPassword(String neo4jPassword) {
		Validate.notEmpty(neo4jPassword, "neo4jPassword cannot be empty");
		this.neo4jPassword = neo4jPassword;
	}

	public final void setNeo4jUri(String neo4jUri) {
		Validate.notEmpty(neo4jUri, "neo4jUri cannot be empty");
		try {
			this.neo4jUri = new URI(neo4jUri);
		}
		catch (Exception e) {
			throw new RuntimeException("invalid neo4j URI", e);
		}
	}

	public final void setNeo4jUsername(String neo4jUsername) {
		Validate.notEmpty(neo4jUsername, "neo4jUsername cannot be empty");
		this.neo4jUsername = neo4jUsername;
	}
}