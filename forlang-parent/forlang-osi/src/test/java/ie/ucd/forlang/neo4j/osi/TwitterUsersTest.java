package ie.ucd.forlang.neo4j.osi;

import static org.junit.Assert.assertEquals;
import ie.ucd.forlang.neo4j.GraphDatabaseUtils;
import ie.ucd.forlang.neo4j.object.Person;
import ie.ucd.forlang.neo4j.object.PersonImpl;
import ie.ucd.forlang.neo4j.object.TwitterAccount;
import ie.ucd.forlang.neo4j.object.TwitterAccountImpl;

import java.util.Date;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
// import org.neo4j.test.server.HTTP;
import org.neo4j.test.TestGraphDatabaseFactory;

public final class TwitterUsersTest {

	private static Person testPerson = null;
	private static TwitterAccount testTwitterAccount = null;
	private GraphDatabaseService graphDb = null;
	private TwitterUsers twitter = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// setup test data
		testPerson = new PersonImpl("Joe Bloogs");
		testTwitterAccount = new TwitterAccountImpl(new Date(), "", 1, 42, false, "", "JBloogs", 3065933806l);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		testPerson = null;
		testTwitterAccount = null;
	}

	@Before
	public final void setUp() throws Exception {
		twitter = new TwitterUsers();
		graphDb = new TestGraphDatabaseFactory().newImpermanentDatabase();
	}

	@After
	public final void tearDown() throws Exception {
		twitter = null;
		graphDb = null;
	}

	@Test
	public final void testGetTwitterAccountOwnersEmpty() {
		assertEquals("total people added: 0", twitter.getTwitterAccountOwners(graphDb, null, null));
	}

	@Test
	public final void testGetTwitterAccountOwnersExclude() {
		GraphDatabaseUtils.addTwitterAccount(graphDb, testTwitterAccount);
		assertEquals("total people added: 0",
				twitter.getTwitterAccountOwners(graphDb, null, new String[] { testTwitterAccount.getScreenName() }));
	}

	@Test
	public final void testGetTwitterAccountOwnersInclude() {
		GraphDatabaseUtils.addTwitterAccount(graphDb, testTwitterAccount);
		assertEquals("total people added: 1",
				twitter.getTwitterAccountOwners(graphDb, new String[] { testTwitterAccount.getScreenName() }, null));
	}

	@Test
	public final void testGetTwitterAccountOwnersOne() {
		GraphDatabaseUtils.addTwitterAccount(graphDb, testTwitterAccount);
		assertEquals("total people added: 1", twitter.getTwitterAccountOwners(graphDb, null, null));
	}

	@Test
	public final void testGetTwitterAccountRelationshipsEmpty() {
		assertEquals("total followers added: 0", twitter.getTwitterAccountRelationships(graphDb, null, null));
	}

	@Test
	public final void testGetTwitterAccountRelationshipsExclude() {
		GraphDatabaseUtils.addTwitterAccount(graphDb, testTwitterAccount);
		assertEquals("total followers added: 0", twitter.getTwitterAccountRelationships(graphDb, null,
				new String[] { testTwitterAccount.getScreenName() }));
	}

	@Test
	public final void testGetTwitterAccountRelationshipsInclude() {
		GraphDatabaseUtils.addTwitterAccount(graphDb, testTwitterAccount);
		assertEquals("total followers added: 43", twitter.getTwitterAccountRelationships(graphDb,
				new String[] { testTwitterAccount.getScreenName() }, null));
	}

	@Test
	public final void testGetTwitterAccountRelationshipsOne() {
		GraphDatabaseUtils.addTwitterAccount(graphDb, testTwitterAccount);
		assertEquals("total followers added: 43", twitter.getTwitterAccountRelationships(graphDb, null, null));
	}

	@Test
	public final void testGetTwitterAccountsEmpty() {
		assertEquals("total accounts added: 0", twitter.getTwitterAccounts(graphDb, null, null));
	}

	@Test
	public final void testGetTwitterAccountsExclude() {
		GraphDatabaseUtils.addPerson(graphDb, new PersonImpl("Joe Bloogs"));
		assertEquals("total accounts added: 0",
				twitter.getTwitterAccounts(graphDb, null, new String[] { testPerson.getName() }));
	}

	@Test
	public final void testGetTwitterAccountsInclude() {
		GraphDatabaseUtils.addPerson(graphDb, testPerson);
		assertEquals("total accounts added: 13",
				twitter.getTwitterAccounts(graphDb, new String[] { testPerson.getName() }, null));
	}

	@Test
	public final void testGetTwitterAccountsOne() {
		GraphDatabaseUtils.addPerson(graphDb, testPerson);
		assertEquals("total accounts added: 13", twitter.getTwitterAccounts(graphDb, null, null));
	}
}