package ie.ucd.forlang.neo4j;

import static org.junit.Assert.*;
import ie.ucd.forlang.neo4j.object.Person;
import ie.ucd.forlang.neo4j.object.PersonImpl;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Node;

public class RestGraphDatabaseServiceTest {

	private static Person testPerson = null;
	private RestGraphDatabaseService rest = null;

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

	@Test
	public final void testCreateNode() {
		Node node = rest.createNode();
		assertNotNull(node.getId());
	}

	@Test
	public void testCreateNodeLabel() {
		Node node = rest.createNode(DynamicLabel.label("TestLabel"));
		assertNotNull(node.getId());
		assertEquals("TestLabel", node.getLabels().iterator().next());
	}

	//@Test
	public void testFindNodesLabel() {
		fail("Not yet implemented");
	}

	//@Test
	public void testFindNodesLabelStringObject() {
		fail("Not yet implemented");
	}
}