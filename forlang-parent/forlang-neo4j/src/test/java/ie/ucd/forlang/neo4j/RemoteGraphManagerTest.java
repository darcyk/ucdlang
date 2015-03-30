package ie.ucd.forlang.neo4j;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public final class RemoteGraphManagerTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	private GraphManager mgr = null;

	@Before
	public final void setUp() throws Exception {
		mgr = new RemoteGraphManager();
	}

	@After
	public final void tearDown() throws Exception {
		mgr = null;
	}

	/** Happy path test */
	@Test
	public final void testInit() {
		mgr.init("http://localhost:7474/db/data");
	}

	/** Should fail it invalid string is passed */
	@Test
	public final void testInitInvalid() {
		thrown.expect(RuntimeException.class);
		thrown.expectMessage("could not initialise remote graph database");
		mgr.init("blah");
	}

	/** Should fail it null object is passed */
	@Test
	public final void testInitNull() {
		thrown.expect(RuntimeException.class);
		thrown.expectMessage("could not initialise remote graph database");
		mgr.init(null);
	}
}