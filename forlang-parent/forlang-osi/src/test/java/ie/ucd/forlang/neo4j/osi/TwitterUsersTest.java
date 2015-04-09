package ie.ucd.forlang.neo4j.osi;

import static org.junit.Assert.assertEquals;
import ie.ucd.forlang.neo4j.GraphDatabaseUtils;
import ie.ucd.forlang.neo4j.object.PersonImpl;
import ie.ucd.forlang.neo4j.object.TwitterAccountImpl;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
// import org.neo4j.test.server.HTTP;
import org.neo4j.test.TestGraphDatabaseFactory;

public final class TwitterUsersTest {

	private TwitterUsers twitter = null;
	private GraphDatabaseService graphDb = null;

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
	public final void testGetTwitterAccountsEmpty() {
		assertEquals("total accounts added: 0", twitter.getTwitterAccounts(graphDb, null, null));
	}

	@Test
	public final void testGetTwitterAccountsOne() {
		GraphDatabaseUtils.addPerson(graphDb, new PersonImpl("Joe Bloogs"));
		assertEquals("total accounts added: 13", twitter.getTwitterAccounts(graphDb, null, null));
	}

	@Test
	public final void testGetTwitterRelationshipsEmpty() {
		assertEquals("total followers added: 0", twitter.getTwitterRelationships(graphDb, null, null));
	}

	@Test
	public final void testGetTwitterRelationshipsOne() {
		GraphDatabaseUtils.addTwitterAccount(graphDb, new TwitterAccountImpl(new Date(), "", 1, 42, false, "",
				"JBloogs", 3065933806l));
		assertEquals("total followers added: 43", twitter.getTwitterRelationships(graphDb, null, null));
	}
}