package ie.ucd.forlang.neo4j;

import static org.junit.Assert.*;

import java.rmi.server.UID;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;

public final class RestGraphDatabaseServiceTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	//private static final Person testPerson = new PersonImpl("Joe Bloggs");
	private RestGraphDatabaseService rest = null;

	@Before
	public final void setUp() throws Exception {
		rest = new RestGraphDatabaseService("http://localhost:7474/db/data", "neo4j", "admin");
	}

	@After
	public final void tearDown() throws Exception {
		rest = null;
	}

	@Test
	public final void testRestGraphDatabaseService() {
		rest = new RestGraphDatabaseService("http://localhost:7474/db/data", "neo4j", "admin");
	}

	@Test
	public final void testBeginTx() {
		assertNotNull(rest.beginTx());
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

	@Test
	public final void testFindNodesByLabelAndProperty() {
		thrown.expect(UnsupportedOperationException.class);
		rest.findNodesByLabelAndProperty(null, null, null);
	}

	@Test
	public final void testFindNodesLabel() {
		Label label = DynamicLabel.label(new UID().toString());
		rest.createNode(label);
		rest.createNode(label);
		rest.createNode(label);
		ResourceIterator<Node> list = rest.findNodes(label);
		assertNotNull(list);
		int i = 0;
		while (list.hasNext()) {
			list.next();
			i++;
		}
		assertEquals(3, i);
	}

	@Test
	public final void testFindNodesLabelProperty() {
		Label label = DynamicLabel.label(new UID().toString());
		Node node = rest.createNode(label);
		assertNotNull(node);
		String key = String.valueOf(System.currentTimeMillis());
		node.setProperty(key, "blah");
		ResourceIterator<Node> list = rest.findNodes(label, key, "blah");
		assertNotNull(list);
		int i = 0;
		while (list.hasNext()) {
			list.next();
			i++;
		}
		assertEquals(1, i);
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