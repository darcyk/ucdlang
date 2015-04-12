package ie.ucd.forlang.neo4j;

import static org.junit.Assert.*;
import ie.ucd.forlang.neo4j.object.Person;
import ie.ucd.forlang.neo4j.object.PersonImpl;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Node;

public class RestGraphDatabaseServiceTest {

	private static Person testPerson = null;
	private RestGraphDatabaseService rest = null;
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		testPerson = new PersonImpl("Joe Bloggs");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		testPerson = null;
	}

	@Before
	public void setUp() throws Exception {
		rest = new RestGraphDatabaseService("http://localhost:7474/db/data", "neo4j", "admin");
	}

	@After
	public void tearDown() throws Exception {
		rest = null;
	}

	//@Test
	public final void testRestGraphDatabaseService() {
		thrown.expect(UnsupportedOperationException.class);
	}

	@Test
	public final void testBeginTx() {
		thrown.expect(UnsupportedOperationException.class);
		rest.beginTx();
	}

	@Test
	public final void testBidirectionalTraversalDescription() {
		thrown.expect(UnsupportedOperationException.class);
		rest.bidirectionalTraversalDescription();
	}

	@Test
	public final void testCreateNode() {
		Node node = rest.createNode();
		assertNotNull(node.getId());
	}

	@Test
	public final void testCreateNodeLabelArray() {
		Node node = rest.createNode(DynamicLabel.label("TestLabel"));
		assertNotNull(node.getId());
		assertEquals("TestLabel", node.getLabels().iterator().next().name());
	}

	@Test
	public final void testExecuteString() {
		thrown.expect(UnsupportedOperationException.class);
		rest.execute(null);
	}

	@Test
	public final void testExecuteStringMapOfStringObject() {
		thrown.expect(UnsupportedOperationException.class);
		rest.execute(null, null);
	}

	@Test
	public final void testFindNode() {
		thrown.expect(UnsupportedOperationException.class);
		rest.findNode(null, null, null);
	}

	//@Test
	public final void testFindNodesLabel() {
		thrown.expect(UnsupportedOperationException.class);
		rest.findNodes(null);
	}

	@Test
	public final void testFindNodesLabelStringObject() {
		thrown.expect(UnsupportedOperationException.class);
		rest.findNodes(null, null, null);
	}

	@Test
	public final void testFindNodesByLabelAndProperty() {
		thrown.expect(UnsupportedOperationException.class);
		rest.findNodesByLabelAndProperty(null, null, null);
	}

	@Test
	public final void testGetAllNodes() {
		thrown.expect(UnsupportedOperationException.class);
		rest.getAllNodes();
	}

	@Test
	public final void testGetNodeById() {
		Node node1 = rest.createNode();
		Node node2 = rest.getNodeById(node1.getId());
		assertEquals(node1.getId(), node2.getId());
	}

	@Test
	public final void testGetRelationshipById() {
		thrown.expect(UnsupportedOperationException.class);
		rest.getRelationshipById(0);
	}

	@Test
	public final void testGetRelationshipTypes() {
		thrown.expect(UnsupportedOperationException.class);
		rest.getRelationshipTypes();
	}

	@Test
	public final void testIndex() {
		thrown.expect(UnsupportedOperationException.class);
		rest.index();
	}

	@Test
	public final void testIsAvailable() {
		thrown.expect(UnsupportedOperationException.class);
		rest.isAvailable(0);
	}

	@Test
	public final void testRegisterKernelEventHandler() {
		thrown.expect(UnsupportedOperationException.class);
		rest.registerKernelEventHandler(null);
	}

	@Test
	public final void testRegisterTransactionEventHandler() {
		thrown.expect(UnsupportedOperationException.class);
		rest.registerTransactionEventHandler(null);
	}

	@Test
	public final void testSchema() {
		thrown.expect(UnsupportedOperationException.class);
		rest.schema();
	}

	@Test
	public final void testShutdown() {
		thrown.expect(UnsupportedOperationException.class);
		rest.shutdown();
	}

	@Test
	public final void testTraversalDescription() {
		thrown.expect(UnsupportedOperationException.class);
		rest.traversalDescription();
	}

	@Test
	public final void testUnregisterKernelEventHandler() {
		thrown.expect(UnsupportedOperationException.class);
		rest.unregisterKernelEventHandler(null);
	}

	@Test
	public final void testUnregisterTransactionEventHandler() {
		thrown.expect(UnsupportedOperationException.class);
		rest.unregisterTransactionEventHandler(null);
	}
}