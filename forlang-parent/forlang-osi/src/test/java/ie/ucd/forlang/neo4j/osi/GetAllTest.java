package ie.ucd.forlang.neo4j.osi;

import static org.junit.Assert.*;

import java.net.URI;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.harness.junit.Neo4jRule;
// import org.neo4j.test.server.HTTP;
import org.neo4j.test.TestGraphDatabaseFactory;

public final class GetAllTest {

	@Rule
	public Neo4jRule neo4j = new Neo4jRule().withFixture("CREATE (admin:Admin)");
	private GetAll all = null;
	private GraphDatabaseService graphDb = null;

	@Before
	public final void setUp() throws Exception {
		all = new GetAll();
		graphDb = new TestGraphDatabaseFactory().newImpermanentDatabase();
	}

	@After
	public final void tearDown() throws Exception {
		all = null;
		graphDb = null;
	}

	@Test
	public final void testGetAllNodesEmpty() {
		Iterable<Node> nodes = all.getAllNodes(graphDb);
		assertFalse(nodes.iterator().hasNext());
	}

	@Test
	public final void testGetAllNodesOne() {
		try (Transaction tx = graphDb.beginTx()) {
			graphDb.createNode();
			tx.success();
		}
		Iterable<Node> nodes = all.getAllNodes(graphDb);
		int i = 0;
		for (Node node : nodes) {
			i++;
		}
		assertEquals(1, i);
	}

	// @Test
	public final void test() {
		// Given
		URI serverURI = neo4j.httpURI();
		// When I access the server
		// HTTP.Response response = HTTP.GET(serverURI.toString());
		// Then it should reply
		// assertEquals(200, response.status());
	}
}
