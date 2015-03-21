package ie.ucd.forlang.neo4j;

import static org.junit.Assert.*;
import ie.ucd.forlang.neo4j.object.EmailAccount;
import ie.ucd.forlang.neo4j.object.EmailAccountImpl;
import ie.ucd.forlang.neo4j.object.EmailMessageImpl;
import ie.ucd.forlang.neo4j.object.GraphObjectType;
import ie.ucd.forlang.neo4j.object.Person;
import ie.ucd.forlang.neo4j.object.PersonImpl;
import ie.ucd.forlang.neo4j.object.RelationshipType;
import ie.ucd.forlang.neo4j.object.TwitterAccountImpl;

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
	public final void testAddEmailAccount() {
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
	public final void testAddEmailAccountNull() {
		thrown.expect(RuntimeException.class);
		thrown.expectMessage("could not add email account to graph database");
		mgr.addEmailAccount(null);
	}

	/** Happy path test */
	@Test
	public final void testAddEmailMessage() {
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
	public final void testAddEmailMessageNull() {
		thrown.expect(RuntimeException.class);
		thrown.expectMessage("could not add email message to graph database");
		mgr.addEmailMessage(null);
	}

	/** Happy path test */
	@Test
	public final void testAddPerson() {
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
	public final void testAddPersonNull() {
		thrown.expect(RuntimeException.class);
		thrown.expectMessage("could not add person to graph database");
		mgr.addPerson(null);
	}

	/** Happy path test */
	@Test
	public final void testAddTwitterAccount() {
		Date date = new Date();
		Node node = mgr.addTwitterAccount(new TwitterAccountImpl(date, "desc", 1, 2, true, "loc", "sname", 22));
		assertNotNull(node);
		try (Transaction tx = graphDb.beginTx()) {
			assertEquals(GraphObjectType.TwitterAccount.toString(), node.getLabels().iterator().next().name());
			assertEquals(date.getTime(), node.getProperty(Constants.PROP_TWITTER_CREATED_AT));
			assertEquals("desc", node.getProperty(Constants.PROP_TWITTER_DESCRIPTION));
			assertEquals(1, node.getProperty(Constants.PROP_TWITTER_FOLLOWERS_COUNT));
			assertEquals(2, node.getProperty(Constants.PROP_TWITTER_FRIENDS_COUNT));
			assertEquals(true, node.getProperty(Constants.PROP_TWITTER_GEO_ENABLED));
			assertEquals("loc", node.getProperty(Constants.PROP_TWITTER_LOCATION));
			assertEquals("sname", node.getProperty(Constants.PROP_TWITTER_SCREEN_NAME));
			assertEquals(22l, node.getProperty(Constants.PROP_TWITTER_ID));
			tx.success();
		}
	}

	/** Should fail it null object is passed */
	@Test
	public final void testAddTwitterAccountNull() {
		thrown.expect(RuntimeException.class);
		thrown.expectMessage("could not add twitter account to graph database");
		mgr.addTwitterAccount(null);
	}

	/** Happy path test */
	@Test
	public final void testDestroy() {
		mgr.destroy();
		assertNull(mgr.getGraphDatabaseService());
	}

	/** Happy path test */
	@Test
	public final void testGetInstance() {
		assertNotNull(EmbeddedGraphManager.getInstance());
		assertNotNull(EmbeddedGraphManager.getInstance().getGraphDatabaseService());
	}

	@Test
	public final void testLinkPersons() {
		Relationship rel = mgr.linkPersons(new PersonImpl("Joe"), new PersonImpl("Dave"));
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
	public final void testLinkPersonToEmailAccount() {
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
	public final void testLinkPersonToTwitterAccount() {
		Date date = new Date();
		Relationship rel = mgr.linkPersonToTwitterAccount(new PersonImpl("Joe"), new TwitterAccountImpl(date, "desc",
				1, 2, true, "loc", "sname", 22));
		try (Transaction tx = graphDb.beginTx()) {
			assertEquals(RelationshipType.PROBABLY_OWNS.toString(), rel.getType().name());
			assertEquals(GraphObjectType.Person.toString(), rel.getStartNode().getLabels().iterator().next().name());
			assertEquals("Joe", rel.getStartNode().getProperty(Constants.PROP_NAME));
			assertEquals(rel.getType(), rel.getStartNode().getRelationships().iterator().next().getType());
			assertEquals(GraphObjectType.TwitterAccount.toString(), rel.getEndNode().getLabels().iterator().next()
					.name());
			assertEquals(22l, rel.getEndNode().getProperty(Constants.PROP_TWITTER_ID));
			assertEquals(rel.getType(), rel.getEndNode().getRelationships().iterator().next().getType());
		}
	}

	@Test
	public final void testListEmailAccounts() {
		mgr.addEmailAccount(new EmailAccountImpl("joe@my.com"));
		mgr.addEmailAccount(new EmailAccountImpl("dave@my.com"));
		mgr.addEmailAccount(new EmailAccountImpl("steve@my.com"));
		List<EmailAccount> list = mgr.listEmailAccounts();
		assertEquals(3, list.size());
	}

	@Test
	public final void testListPeople() {
		mgr.addPerson(new PersonImpl("joe"));
		mgr.addPerson(new PersonImpl("dave"));
		mgr.addPerson(new PersonImpl("steve"));
		List<Person> list = mgr.listPeople();
		assertEquals(3, list.size());
	}
}