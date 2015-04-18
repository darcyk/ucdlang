package ie.ucd.forlang.neo4j;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import ie.ucd.forlang.neo4j.object.EmailAccount;
import ie.ucd.forlang.neo4j.object.EmailAccountImpl;
import ie.ucd.forlang.neo4j.object.EmailMessage;
import ie.ucd.forlang.neo4j.object.EmailMessageImpl;
import ie.ucd.forlang.neo4j.object.GraphObjectType;
import ie.ucd.forlang.neo4j.object.Person;
import ie.ucd.forlang.neo4j.object.PersonImpl;
import ie.ucd.forlang.neo4j.object.RelationshipType;
import ie.ucd.forlang.neo4j.object.TwitterAccount;
import ie.ucd.forlang.neo4j.object.TwitterAccountImpl;

import java.rmi.server.UID;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.test.TestGraphDatabaseFactory;

public final class GraphDatabaseUtilsTest {

	// testing data
	private static Date now = null;
	private static EmailAccount testEmailAccount1 = null, testEmailAccount2 = null;
	private static EmailMessage testEmailMessage1 = null;
	private static Person testPerson1 = null, testPerson2 = null;
	private static TwitterAccount testTwitterAccount1 = null, testTwitterAccount2 = null;
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	private GraphDatabaseService graphDb = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// setup test data
		now = new Date();
		testEmailAccount1 = new EmailAccountImpl("me@my.com");
		testEmailAccount2 = new EmailAccountImpl("you@my.com");
		testEmailMessage1 = new EmailMessageImpl(new UID().toString(), "sender1@my.com",
				new String[] { "receiver@my.com" }, "a subject", now);
		// testEmailMessage2 = new EmailMessageImpl("sender2@my.com", new String[] { "receiver@my.com" }, "a subject",
		// now);
		testPerson1 = new PersonImpl("Joe Bloggs");
		testPerson2 = new PersonImpl("Jane Bloggs");
		testTwitterAccount1 = new TwitterAccountImpl(now, "desc", 1, 2, true, "loc", "@name1", 22);
		testTwitterAccount2 = new TwitterAccountImpl(now, "desc", 1, 2, true, "loc", "@name2", 23);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		now = null;
		testEmailAccount1 = null;
		testEmailAccount2 = null;
		testEmailMessage1 = null;
		// testEmailMessage2 = null;
		testPerson1 = null;
		testPerson2 = null;
		testTwitterAccount1 = null;
		testTwitterAccount2 = null;
	}

	@Before
	public final void setUp() throws Exception {
		graphDb = new TestGraphDatabaseFactory().newImpermanentDatabase();
	}

	@After
	public final void tearDown() throws Exception {
		graphDb.shutdown();
		graphDb = null;
	}

	/** Happy path test */
	@Test
	public final void testAddEmailAccount() {
		Node node = GraphDatabaseUtils.addEmailAccount(graphDb, testEmailAccount1);
		assertNotNull(node);
		try (Transaction tx = graphDb.beginTx()) {
			assertEquals(GraphObjectType.EmailAccount.toString(), node.getLabels().iterator().next().name());
			assertEquals(testEmailAccount1.getEmailAddress(), node.getProperty(Constants.PROP_EMAIL_ADDRESS));
			tx.success();
		}
	}

	/** Make sure duplicate email accounts aren't added */
	@Test
	public final void testAddEmailAccountDuplicate() {
		Node node1 = GraphDatabaseUtils.addEmailAccount(graphDb, testEmailAccount1);
		assertNotNull(node1);
		Node node2 = GraphDatabaseUtils.addEmailAccount(graphDb, testEmailAccount1);
		assertNotNull(node2);
		assertEquals(node1.getId(), node2.getId());
	}

	/** Should fail it null object is passed */
	@Test
	public final void testAddEmailAccountNull() {
		thrown.expect(RuntimeException.class);
		thrown.expectMessage("could not add email account to graph database");
		GraphDatabaseUtils.addEmailAccount(graphDb, null);
	}

	/** Happy path test */
	@Test
	public final void testAddEmailMessage() {
		Node node = GraphDatabaseUtils.addEmailMessage(graphDb, testEmailMessage1);
		assertNotNull(node);
		try (Transaction tx = graphDb.beginTx()) {
			assertEquals(GraphObjectType.EmailMessage.toString(), node.getLabels().iterator().next().name());
			assertEquals(testEmailMessage1.getUid(), node.getProperty(Constants.PROP_MAIL_UID));
			assertEquals(testEmailMessage1.getSender(), node.getProperty(Constants.PROP_MAIL_SENDER));
			assertEquals(testEmailMessage1.getRecipients()[0],
					((String[]) node.getProperty(Constants.PROP_MAIL_RECIPIENTS))[0]);
			assertEquals(testEmailMessage1.getSubject(), node.getProperty(Constants.PROP_MAIL_SUBJECT));
			assertEquals(testEmailMessage1.getDateSent().getTime(), node.getProperty(Constants.PROP_MAIL_DATE));
			tx.success();
		}
	}

	/** Make sure duplicate email messages aren't added */
	@Test
	public final void testAddEmailMessageDuplicate() {
		Node node1 = GraphDatabaseUtils.addEmailMessage(graphDb, testEmailMessage1);
		assertNotNull(node1);
		Node node2 = GraphDatabaseUtils.addEmailMessage(graphDb, testEmailMessage1);
		assertNotNull(node2);
		assertEquals(node1.getId(), node2.getId());
	}

	/** Should fail it null object is passed */
	@Test
	public final void testAddEmailMessageNull() {
		thrown.expect(RuntimeException.class);
		thrown.expectMessage("could not add email message to graph database");
		GraphDatabaseUtils.addEmailMessage(graphDb, null);
	}

	/** Happy path test */
	@Test
	public final void testAddPerson() {
		Node node = GraphDatabaseUtils.addPerson(graphDb, new PersonImpl("Joe Bloggs"));
		assertNotNull(node);
		try (Transaction tx = graphDb.beginTx()) {
			assertEquals(GraphObjectType.Person.toString(), node.getLabels().iterator().next().name());
			assertEquals("Joe Bloggs", node.getProperty(Constants.PROP_NAME));
			tx.success();
		}
	}

	/** Make sure duplicate persons aren't added */
	@Test
	public final void testAddPersonDuplicate() {
		Node node1 = GraphDatabaseUtils.addPerson(graphDb, testPerson1);
		assertNotNull(node1);
		Node node2 = GraphDatabaseUtils.addPerson(graphDb, testPerson1);
		assertNotNull(node2);
		assertEquals(node1.getId(), node2.getId());
	}

	/** Should fail it null object is passed */
	@Test
	public final void testAddPersonNull() {
		thrown.expect(RuntimeException.class);
		thrown.expectMessage("could not add person to graph database");
		GraphDatabaseUtils.addPerson(graphDb, null);
	}

	/** Happy path test */
	@Test
	public final void testAddTwitterAccount() {
		Date date = new Date();
		Node node = GraphDatabaseUtils.addTwitterAccount(graphDb, new TwitterAccountImpl(date, "desc", 1, 2, true,
				"loc", "@sname", 2730631792l));
		assertNotNull(node);
		try (Transaction tx = graphDb.beginTx()) {
			assertEquals(GraphObjectType.TwitterAccount.toString(), node.getLabels().iterator().next().name());
			assertEquals(date.getTime(), node.getProperty(Constants.PROP_TWITTER_CREATED_AT));
			assertEquals("desc", node.getProperty(Constants.PROP_TWITTER_DESCRIPTION));
			assertEquals(1, node.getProperty(Constants.PROP_TWITTER_FOLLOWERS_COUNT));
			assertEquals(2, node.getProperty(Constants.PROP_TWITTER_FRIENDS_COUNT));
			assertEquals(true, node.getProperty(Constants.PROP_TWITTER_GEO_ENABLED));
			assertEquals("loc", node.getProperty(Constants.PROP_TWITTER_LOCATION));
			assertEquals("@sname", node.getProperty(Constants.PROP_TWITTER_SCREEN_NAME));
			assertEquals(2730631792l, node.getProperty(Constants.PROP_TWITTER_ID));
			tx.success();
		}
	}

	/** Make sure duplicate twitter accounts aren't added */
	@Test
	public final void testAddTwitterAccountDuplicate() {
		Node node1 = GraphDatabaseUtils.addTwitterAccount(graphDb, testTwitterAccount1);
		assertNotNull(node1);
		Node node2 = GraphDatabaseUtils.addTwitterAccount(graphDb, testTwitterAccount1);
		assertNotNull(node2);
		assertEquals(node1.getId(), node2.getId());
	}

	/** Should fail it null object is passed */
	@Test
	public final void testAddTwitterAccountNull() {
		thrown.expect(RuntimeException.class);
		thrown.expectMessage("could not add twitter account to graph database");
		GraphDatabaseUtils.addTwitterAccount(graphDb, null);
	}

	@Test
	public final void testLinkEmailChain() {
		List<Relationship> rels = GraphDatabaseUtils.linkEmailChain(graphDb, testEmailMessage1, testEmailAccount1,
				Arrays.asList(testEmailAccount2));
		try (Transaction tx = graphDb.beginTx()) {
			assertEquals(RelationshipType.SENT.toString(), rels.get(0).getType().name());
			assertEquals(GraphObjectType.EmailAccount.toString(), rels.get(0).getStartNode().getLabels().iterator()
					.next().name());
			assertEquals(GraphObjectType.EmailMessage.toString(), rels.get(0).getEndNode().getLabels().iterator()
					.next().name());
			assertEquals(testEmailAccount1.getEmailAddress(),
					rels.get(0).getStartNode().getProperty(Constants.PROP_EMAIL_ADDRESS));
			assertEquals(RelationshipType.RECEIVED.toString(), rels.get(1).getType().name());
			assertEquals(GraphObjectType.EmailMessage.toString(), rels.get(1).getStartNode().getLabels().iterator()
					.next().name());
			assertEquals(GraphObjectType.EmailAccount.toString(), rels.get(1).getEndNode().getLabels().iterator()
					.next().name());
			assertEquals(testEmailMessage1.getUid(), rels.get(1).getStartNode().getProperty(Constants.PROP_MAIL_UID));
			assertEquals(testEmailAccount2.getEmailAddress(),
					rels.get(1).getEndNode().getProperty(Constants.PROP_EMAIL_ADDRESS));
			tx.success();
		}
	}

	@Test
	public final void testLinkEmailChainDuplicate() {
		List<Relationship> rels1 = GraphDatabaseUtils.linkEmailChain(graphDb, testEmailMessage1, testEmailAccount1,
				Arrays.asList(testEmailAccount2));
		assertEquals(2, rels1.size());
		List<Relationship> rels2 = GraphDatabaseUtils.linkEmailChain(graphDb, testEmailMessage1, testEmailAccount1,
				Arrays.asList(testEmailAccount2));
		assertEquals(2, rels2.size());
		assertEquals(rels1.get(0).getId(), rels2.get(0).getId());
		assertEquals(rels1.get(1).getId(), rels2.get(1).getId());
	}

	@Test
	public final void testLinkPersons() {
		Relationship rel = GraphDatabaseUtils.linkPersons(graphDb, testPerson1, testPerson2);
		try (Transaction tx = graphDb.beginTx()) {
			assertEquals(RelationshipType.KNOWNS.toString(), rel.getType().name());
			assertEquals(GraphObjectType.Person.toString(), rel.getStartNode().getLabels().iterator().next().name());
			assertEquals(testPerson1.getName(), rel.getStartNode().getProperty(Constants.PROP_NAME));
			assertEquals(rel.getType(), rel.getStartNode().getRelationships().iterator().next().getType());
			assertEquals(GraphObjectType.Person.toString(), rel.getEndNode().getLabels().iterator().next().name());
			assertEquals(testPerson2.getName(), rel.getEndNode().getProperty(Constants.PROP_NAME));
			assertEquals(rel.getType(), rel.getEndNode().getRelationships().iterator().next().getType());
			tx.success();
		}
	}

	@Test
	public final void testLinkPersonsDuplicate() {
		Relationship rel1 = GraphDatabaseUtils.linkPersons(graphDb, testPerson1, testPerson2);
		Relationship rel2 = GraphDatabaseUtils.linkPersons(graphDb, testPerson1, testPerson2);
		assertEquals(rel1.getId(), rel2.getId());
	}

	@Test
	public final void testLinkPersonToEmailAccount() {
		Relationship rel = GraphDatabaseUtils.linkPersonToEmailAccount(graphDb, new PersonImpl("Joe"),
				new EmailAccountImpl("joe@my.com"));
		try (Transaction tx = graphDb.beginTx()) {
			assertEquals(RelationshipType.OWNS.toString(), rel.getType().name());
			assertEquals(GraphObjectType.Person.toString(), rel.getStartNode().getLabels().iterator().next().name());
			assertEquals("Joe", rel.getStartNode().getProperty(Constants.PROP_NAME));
			assertEquals(rel.getType(), rel.getStartNode().getRelationships().iterator().next().getType());
			assertEquals(GraphObjectType.EmailAccount.toString(), rel.getEndNode().getLabels().iterator().next().name());
			assertEquals("joe@my.com", rel.getEndNode().getProperty(Constants.PROP_EMAIL_ADDRESS));
			assertEquals(rel.getType(), rel.getEndNode().getRelationships().iterator().next().getType());
			tx.success();
		}
	}

	@Test
	public final void testLinkPersonToEmailAccountDuplicate() {
		Relationship rel1 = GraphDatabaseUtils.linkPersonToEmailAccount(graphDb, new PersonImpl("Joe"),
				new EmailAccountImpl("joe@my.com"));
		Relationship rel2 = GraphDatabaseUtils.linkPersonToEmailAccount(graphDb, new PersonImpl("Joe"),
				new EmailAccountImpl("joe@my.com"));
		assertEquals(rel1.getId(), rel2.getId());
	}

	@Test
	public final void testLinkPersonToTwitterAccountRel() {
		Relationship rel = GraphDatabaseUtils.linkPersonToTwitterAccount(graphDb, testPerson1, testTwitterAccount1,
				RelationshipType.PROBABLY_OWNS);
		try (Transaction tx = graphDb.beginTx()) {
			assertEquals(RelationshipType.PROBABLY_OWNS.toString(), rel.getType().name());
			assertEquals(GraphObjectType.Person.toString(), rel.getStartNode().getLabels().iterator().next().name());
			assertEquals(testPerson1.getName(), rel.getStartNode().getProperty(Constants.PROP_NAME));
			assertEquals(rel.getType(), rel.getStartNode().getRelationships().iterator().next().getType());
			assertEquals(GraphObjectType.TwitterAccount.toString(), rel.getEndNode().getLabels().iterator().next()
					.name());
			assertEquals(testTwitterAccount1.getTwitterId(), rel.getEndNode().getProperty(Constants.PROP_TWITTER_ID));
			assertEquals(rel.getType(), rel.getEndNode().getRelationships().iterator().next().getType());
			tx.success();
		}
		rel = GraphDatabaseUtils.linkPersonToTwitterAccount(graphDb, testPerson1, testTwitterAccount1,
				RelationshipType.OWNS);
		try (Transaction tx = graphDb.beginTx()) {
			assertEquals(RelationshipType.OWNS.toString(), rel.getType().name());
			tx.success();
		}
	}

	@Test
	public final void testLinkPersonToTwitterAccountInvalid() {
		thrown.expect(RuntimeException.class);
		thrown.expectMessage("could not create person to twitter account relationship");
		GraphDatabaseUtils.linkPersonToTwitterAccount(graphDb, testPerson1, testTwitterAccount1,
				RelationshipType.FOLLOWS);
	}

	@Test
	public final void testLinkPersonToTwitterAccountNull() {
		thrown.expect(RuntimeException.class);
		thrown.expectMessage("could not create person to twitter account relationship");
		GraphDatabaseUtils.linkPersonToTwitterAccount(graphDb, testPerson1, testTwitterAccount1, null);
	}

	@Test
	public final void testLinkTwitterAccountsRel() {
		Relationship rel = GraphDatabaseUtils.linkTwitterAccounts(graphDb, testTwitterAccount1, testTwitterAccount2,
				RelationshipType.FOLLOWS);
		try (Transaction tx = graphDb.beginTx()) {
			assertEquals(RelationshipType.FOLLOWS.toString(), rel.getType().name());
			assertEquals(GraphObjectType.TwitterAccount.toString(), rel.getStartNode().getLabels().iterator().next()
					.name());
			assertEquals(testTwitterAccount1.getTwitterId(), rel.getStartNode().getProperty(Constants.PROP_TWITTER_ID));
			assertEquals(rel.getType(), rel.getStartNode().getRelationships().iterator().next().getType());
			assertEquals(GraphObjectType.TwitterAccount.toString(), rel.getEndNode().getLabels().iterator().next()
					.name());
			assertEquals(testTwitterAccount2.getTwitterId(), rel.getEndNode().getProperty(Constants.PROP_TWITTER_ID));
			assertEquals(rel.getType(), rel.getEndNode().getRelationships().iterator().next().getType());
			tx.success();
		}
		rel = GraphDatabaseUtils.linkTwitterAccounts(graphDb, testTwitterAccount1, testTwitterAccount2,
				RelationshipType.IS_FOLLOWED_BY);
		try (Transaction tx = graphDb.beginTx()) {
			assertEquals(RelationshipType.IS_FOLLOWED_BY.toString(), rel.getType().name());
			tx.success();
		}
	}

	@Test
	public final void testLinkTwitterAccountsInvalid() {
		thrown.expect(RuntimeException.class);
		thrown.expectMessage("could not create twitter account to twitter account relationship");
		GraphDatabaseUtils
				.linkTwitterAccounts(graphDb, testTwitterAccount1, testTwitterAccount2, RelationshipType.OWNS);
	}

	@Test
	public final void testLinkTwitterAccountsNull() {
		thrown.expect(RuntimeException.class);
		thrown.expectMessage("could not create twitter account to twitter account relationship");
		GraphDatabaseUtils
				.linkTwitterAccounts(graphDb, testTwitterAccount1, testTwitterAccount2, RelationshipType.OWNS);
	}

	@Test
	public final void testListEmailAccounts() {
		GraphDatabaseUtils.addEmailAccount(graphDb, testEmailAccount1);
		GraphDatabaseUtils.addEmailAccount(graphDb, testEmailAccount2);
		List<EmailAccount> list = GraphDatabaseUtils.listEmailAccounts(graphDb);
		assertEquals(2, list.size());
	}

	@Test
	public final void testListPeople() {
		GraphDatabaseUtils.addPerson(graphDb, testPerson1);
		GraphDatabaseUtils.addPerson(graphDb, testPerson2);
		List<Person> list = GraphDatabaseUtils.listPeople(graphDb);
		assertEquals(2, list.size());
	}

	@Test
	public final void testListTwitterAccounts() {
		GraphDatabaseUtils.addTwitterAccount(graphDb, testTwitterAccount1);
		GraphDatabaseUtils.addTwitterAccount(graphDb, testTwitterAccount2);
		List<TwitterAccount> list = GraphDatabaseUtils.listTwitterAccounts(graphDb);
		assertEquals(2, list.size());
	}
}