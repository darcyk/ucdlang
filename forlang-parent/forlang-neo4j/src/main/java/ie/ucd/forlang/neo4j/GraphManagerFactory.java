package ie.ucd.forlang.neo4j;

public final class GraphManagerFactory {

	/**
	 * Factory method for <code>GraphManager</code> creation
	 * 
	 * @return GraphManager
	 */
	public static final GraphManager newGraphManager() {
		return new RemoteGraphManager();
	}
}
