package ie.ucd.forlang.neo4j;

import static org.junit.Assert.*;
import ie.ucd.forlang.neo4j.object.EmailAccount;
import ie.ucd.forlang.neo4j.object.EmailAccountImpl;
import ie.ucd.forlang.neo4j.object.EmailMessageImpl;
import ie.ucd.forlang.neo4j.object.GraphObjectType;
import ie.ucd.forlang.neo4j.object.Person;
import ie.ucd.forlang.neo4j.object.PersonImpl;
import ie.ucd.forlang.neo4j.object.RelationshipType;

import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.test.TestGraphDatabaseFactory;

public final class EmbeddedGraphManagerTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	private GraphDatabaseService graphDb = null;
	private EmbeddedGraphManager mgr = null;

	@Before
	public final void setUp() throws Exception {
		mgr = EmbeddedGraphManager.getInstance();
		graphDb = new TestGraphDatabaseFactory().newImpermanentDatabase();
		mgr.setGraphDb(graphDb);
	}

	@After
	public final void tearDown() throws Exception {
		graphDb.shutdown();
		graphDb = null;
		mgr = null;
	}

	/** Happy path test */
	@Test
	public void testAddEmailAccount() {
		Node node = mgr.addEmailAccount(new EmailAccountImpl("me@my.com"));
		assertNotNull(node);
		try (Transaction tx = graphDb.beginTx()) {
			assertEquals(GraphObjectType.EmailAccount.toString(), node.getLabels().iterator().next().name());
			assertEquals("me@my.com", node.getProperty(Constants.PROP_EMAIL_ADDRESS));
			tx.success();
		}
	}

	/** Should fail it null object is passed */
	@Test
	public void testAddEmailAccountNull() {
		thrown.expect(RuntimeException.class);
		thrown.expectMessage("could not add email account to node to graph database");
		mgr.addEmailAccount(null);
	}

	/** Happy path test */
	@Test
	public void testAddEmailMessage() {
		Date date = new Date();
		Node node = mgr.addEmailMessage(new EmailMessageImpl("sender@my.com", new String[] { "receiver@my.com" },
				"a subject", date));
		assertNotNull(node);
		try (Transaction tx = graphDb.beginTx()) {
			assertEquals(GraphObjectType.EmailMessage.toString(), node.getLabels().iterator().next().name());
			assertEquals("sender@my.com", node.getProperty(Constants.PROP_SENDER));
			assertEquals("receiver@my.com", ((String[]) node.getProperty(Constants.PROP_RECIPIENTS))[0]);
			assertEquals("a subject", node.getProperty(Constants.PROP_SUBJECT));
			assertEquals(date.getTime(), node.getProperty(Constants.PROP_DATE_SENT));
			tx.success();
		}
	}

	/** Should fail it null object is passed */
	@Test
	public void testAddEmailMessageNull() {
		thrown.expect(RuntimeException.class);
		thrown.expectMessage("could not add email message to node to graph database");
		mgr.addEmailMessage(null);
	}

	/** Happy path test */
	@Test
	public void testAddPerson() {
		Node node = mgr.addPerson(new PersonImpl("Joe Bloggs"));
		assertNotNull(node);
		try (Transaction tx = graphDb.beginTx()) {
			assertEquals(GraphObjectType.Person.toString(), node.getLabels().iterator().next().name());
			assertEquals("Joe Bloggs", node.getProperty(Constants.PROP_NAME));
			tx.success();
		}
	}

	/** Should fail it null object is passed */
	@Test
	public void testAddPersonNull() {
		thrown.expect(RuntimeException.class);
		thrown.expectMessage("could not add person to node to graph database");
		mgr.addPerson(null);
	}

	/** Happy path test */
	@Test
	public void testDestroy() {
		mgr.destroy();
		assertNull(mgr.getGraphDatabaseService());
	}

	/** Happy path test */
	@Test
	public void testGetInstance() {
		assertNotNull(EmbeddedGraphManager.getInstance());
		assertNotNull(EmbeddedGraphManager.getInstance().getGraphDatabaseService());
	}

	@Test
	public void testLinkPerson() {
		Relationship rel = mgr.linkPerson(new PersonImpl("Joe"), new PersonImpl("Dave"));
		try (Transaction tx = graphDb.beginTx()) {
			assertEquals(RelationshipType.KNOWNS.toString(), rel.getType().name());
			assertEquals(GraphObjectType.Person.toString(), rel.getStartNode().getLabels().iterator().next().name());
			assertEquals("Joe", rel.getStartNode().getProperty(Constants.PROP_NAME));
			assertEquals(rel.getType(), rel.getStartNode().getRelationships().iterator().next().getType());
			assertEquals(GraphObjectType.Person.toString(), rel.getEndNode().getLabels().iterator().next().name());
			assertEquals("Dave", rel.getEndNode().getProperty(Constants.PROP_NAME));
			assertEquals(rel.getType(), rel.getEndNode().getRelationships().iterator().next().getType());
		}
	}

	@Test
	public void testLinkPersonToEmailAccount() {
		Relationship rel = mgr.linkPersonToEmailAccount(new PersonImpl("Joe"), new EmailAccountImpl("joe@my.com"));
		try (Transaction tx = graphDb.beginTx()) {
			assertEquals(RelationshipType.OWNS.toString(), rel.getType().name());
			assertEquals(GraphObjectType.Person.toString(), rel.getStartNode().getLabels().iterator().next().name());
			assertEquals("Joe", rel.getStartNode().getProperty(Constants.PROP_NAME));
			assertEquals(rel.getType(), rel.getStartNode().getRelationships().iterator().next().getType());
			assertEquals(GraphObjectType.EmailAccount.toString(), rel.getEndNode().getLabels().iterator().next().name());
			assertEquals("joe@my.com", rel.getEndNode().getProperty(Constants.PROP_EMAIL_ADDRESS));
			assertEquals(rel.getType(), rel.getEndNode().getRelationships().iterator().next().getType());
		}
	}

	@Test
	public void testListEmailAccounts() {
		mgr.addEmailAccount(new EmailAccountImpl("joe@my.com"));
		mgr.addEmailAccount(new EmailAccountImpl("dave@my.com"));
		mgr.addEmailAccount(new EmailAccountImpl("steve@my.com"));
		List<EmailAccount> list = mgr.listEmailAccounts();
		assertEquals(3, list.size());
	}

	@Test
	public void testListPeople() {
		mgr.addPerson(new PersonImpl("joe"));
		mgr.addPerson(new PersonImpl("dave"));
		mgr.addPerson(new PersonImpl("steve"));
		List<Person> list = mgr.listPeople();
		assertEquals(3, list.size());
	}
}