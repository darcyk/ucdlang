package ie.ucd.forlang.neo4j;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.test.TestGraphDatabaseFactory;

public class BasicTest {

	private GraphDatabaseService graphDb = null;

	// @BeforeClass
	// public static void setUpBeforeClass() throws Exception {
	// }
	//
	// @AfterClass
	// public static void tearDownAfterClass() throws Exception {
	// }
	@Before
	public void setUp() throws Exception {
		graphDb = new TestGraphDatabaseFactory().newImpermanentDatabase();
	}

	@After
	public void tearDown() throws Exception {
		graphDb.shutdown();
	}

	@Test
	public void test() {
		Node n = null;
		try (Transaction tx = graphDb.beginTx()) {
			n = graphDb.createNode();
			n.setProperty("name", "Nancy");
			tx.success();
		}
		// The node should have a valid id
		assertThat(n.getId(), is(greaterThan(-1L)));
		// Retrieve a node by using the id of the created node. The id's and
		// property should match.
		try (Transaction tx = graphDb.beginTx()) {
			Node foundNode = graphDb.getNodeById(n.getId());
			assertThat(foundNode.getId(), is(n.getId()));
			assertThat((String) foundNode.getProperty("name"), is("Nancy"));
		}
	}
}