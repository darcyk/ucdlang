package ie.ucd.forlang.neo4j;

import org.neo4j.rest.graphdb.RestGraphDatabase;

/** Singleton */
public final class RemoteGraphManager extends AbstractGraphManager {

	public RemoteGraphManager() {
		super();
	}

	/** @see GraphManager#destroy() */
	@Override
	public final void destroy() throws RuntimeException {
		graphDb = null;
	}

	/** @see GraphManager#init(String) */
	@Override
	public final void init(String dbUrl) throws RuntimeException {
		try {
			if (graphDb == null) {
				Utils.validDatabaseUrlRoot(dbUrl);
				graphDb = new RestGraphDatabase(dbUrl);
			}
		}
		catch (Exception e) {
			throw new RuntimeException("could not initialise remote graph database", e);
		}
	}
}