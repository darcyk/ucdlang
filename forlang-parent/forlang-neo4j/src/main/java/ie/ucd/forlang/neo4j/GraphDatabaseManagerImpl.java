package ie.ucd.forlang.neo4j;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public final class GraphDatabaseManagerImpl {

	public static final void main(String[] args) {
		GraphDatabaseService graphDb = null;
		try {
			graphDb = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder("C:/data/neo4j")
					.loadPropertiesFromFile("config.properties").newGraphDatabase();
			registerShutdownHook(graphDb);
			addInfo(graphDb);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			graphDb.shutdown();
			graphDb = null;
		}
	}

	private static final void addInfo(GraphDatabaseService graphDb) {
		Node node1 = null, node2 = null;
		try (Transaction tx = graphDb.beginTx()) {
			// Database operations go here
			node1 = graphDb.createNode();
			node1.setProperty("key", "value");
			node2 = graphDb.createNode();
			node2.setProperty("key2", "value2");
			node1.createRelationshipTo(node2, RelationshipType.KNOWNS);
			tx.success();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			node1 = null;
		}
	}

	private static final void registerShutdownHook(final GraphDatabaseService graphDb) {
		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {
				graphDb.shutdown();
			}
		});
	}
}