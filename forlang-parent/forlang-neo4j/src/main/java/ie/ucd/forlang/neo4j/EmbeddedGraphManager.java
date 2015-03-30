package ie.ucd.forlang.neo4j;

import java.io.File;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

/** Singleton */
public final class EmbeddedGraphManager extends AbstractGraphManager {

	private static final AbstractGraphManager service = new EmbeddedGraphManager();

	/** Singleton constructor */
	private EmbeddedGraphManager() {
		super();
	}

	/**
	 * Singleton accessor
	 * 
	 * @return GraphManager The singleton instance
	 */
	public static final EmbeddedGraphManager getInstance() {
		return (EmbeddedGraphManager) service;
	}

	private static final void registerShutdownHook(final GraphDatabaseService graphDb) {
		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {
				graphDb.shutdown();
			}
		});
	}

	/** @see GraphManager#destroy() */
	@Override
	public final synchronized void destroy() throws RuntimeException {
		try {
			if (graphDb != null) {
				graphDb.shutdown();
			}
		}
		catch (Exception e) {
			throw new RuntimeException("could not close graph database", e);
		}
		finally {
			graphDb = null;
		}
	}

	/** @see GraphManager#init(String) */
	@Override
	public final synchronized void init(String dbRoot) throws RuntimeException {
		File root = null;
		try {
			if (graphDb == null) {
				root = Utils.validDatabaseRoot(dbRoot);
				graphDb = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder(root.getPath()).newGraphDatabase();
				registerShutdownHook(graphDb);
			}
		}
		catch (Exception e) {
			throw new RuntimeException("could not initialise embedded graph database", e);
		}
		finally {
			root = null;
		}
	}
}