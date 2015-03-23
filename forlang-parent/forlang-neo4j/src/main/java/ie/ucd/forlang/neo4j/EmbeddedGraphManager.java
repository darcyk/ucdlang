package ie.ucd.forlang.neo4j;

import ie.ucd.forlang.neo4j.object.EmailAccount;
import ie.ucd.forlang.neo4j.object.EmailMessage;
import ie.ucd.forlang.neo4j.object.GraphObject;
import ie.ucd.forlang.neo4j.object.GraphObjectType;
import ie.ucd.forlang.neo4j.object.Person;
import ie.ucd.forlang.neo4j.object.RelationshipType;
import ie.ucd.forlang.neo4j.object.TwitterAccount;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang.Validate;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.tooling.GlobalGraphOperations;

/** Singleton */
public final class EmbeddedGraphManager implements GraphManager {

	private static final EmbeddedGraphManager service = new EmbeddedGraphManager();
	private GraphDatabaseService graphDb = null;

	/** Singleton constructor */
	private EmbeddedGraphManager() {
		super();
	}

	/**
	 * Singleton accessor
	 * 
	 * @return EmbeddedGraphManager The singleton instance
	 */
	public static final EmbeddedGraphManager getInstance() {
		return service;
	}

	private static final void registerShutdownHook(final GraphDatabaseService graphDb) {
		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {
				graphDb.shutdown();
			}
		});
	}

	/** @see GraphManager#addEmailAccount(EmailAccount) */
	@Override
	public final Node addEmailAccount(EmailAccount account) throws RuntimeException {
		try {
			return addNode(account);
		}
		catch (Exception e) {
			throw new RuntimeException("could not add email account to graph database", e);
		}
	}

	/** @see GraphManager#addEmailMessage(EmailMessage) */
	@Override
	public final Node addEmailMessage(EmailMessage msg) throws RuntimeException {
		try {
			return addNode(msg);
		}
		catch (Exception e) {
			throw new RuntimeException("could not add email message to graph database", e);
		}
	}

	/** @see GraphManager#addPerson(Person) */
	@Override
	public final Node addPerson(Person person) throws RuntimeException {
		try {
			return addNode(person);
		}
		catch (Exception e) {
			throw new RuntimeException("could not add person to graph database", e);
		}
	}

	/** @see GraphManager#addTwitterAccount(TwitterAccount) */
	@Override
	public final Node addTwitterAccount(TwitterAccount account) throws RuntimeException {
		try {
			return addNode(account);
		}
		catch (Exception e) {
			throw new RuntimeException("could not add twitter account to graph database", e);
		}
	}

	/** @see GraphManager#destroy() */
	@Override
	public final void destroy() throws RuntimeException {
		try {
			if (graphDb != null) {
				graphDb.shutdown();
			}
		}
		catch (Exception e) {
			throw new RuntimeException("could not close graph database", e);
		}
		finally {
			graphDb = null;
		}
	}

	/** @see GraphManager#getGraphDatabaseService() */
	@Override
	public final GraphDatabaseService getGraphDatabaseService() {
		return graphDb;
	}

	/** @see GraphManager#init(File) */
	@Override
	public final void init(File dbRoot) throws RuntimeException {
		try {
			// graphDb = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder("C:/data/neo4j").newGraphDatabase();
			Utils.validDatabaseRoot(dbRoot);
			graphDb = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder(dbRoot.getPath()).newGraphDatabase();
			registerShutdownHook(graphDb);
		}
		catch (Exception e) {
			throw new RuntimeException("could not initialise graph database", e);
		}
	}

	/** @see GraphManager#linkEmailChain(EmailMessage, EmailAccount, List) */
	@Override
	public Relationship linkEmailChain(EmailMessage msg, EmailAccount from, List<EmailAccount> to)
			throws RuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

	/** @see GraphManager#linkPersons(Person, Person) */
	@Override
	public final Relationship linkPersons(Person person, Person knows) throws RuntimeException {
		try {
			return createRelationship(person, knows, RelationshipType.KNOWNS);
		}
		catch (Exception e) {
			throw new RuntimeException("could not create person to person relationship", e);
		}
	}

	/** @see GraphManager#linkPersonToEmailAccount(Person, EmailAccount) */
	@Override
	public Relationship linkPersonToEmailAccount(Person person, EmailAccount account) throws RuntimeException {
		try {
			return createRelationship(person, account, RelationshipType.OWNS);
		}
		catch (Exception e) {
			throw new RuntimeException("could not create person to email account relationship", e);
		}
	}

	/** @see GraphManager#linkPersonToTwitterAccount(Person, TwitterAccount) */
	@Override
	public final Relationship linkPersonToTwitterAccount(Person person, TwitterAccount account) throws RuntimeException {
		try {
			return createRelationship(person, account, RelationshipType.PROBABLY_OWNS);
		}
		catch (Exception e) {
			throw new RuntimeException("could not create person to twitter account relationship", e);
		}
	}

	/** @see GraphManager#linkTwitterAccounts(TwitterAccount, TwitterAccount) */
	@Override
	public final Relationship linkTwitterAccounts(TwitterAccount follower, TwitterAccount follows)
			throws RuntimeException {
		try {
			return createRelationship(follower, follows, RelationshipType.FOLLOWS);
		}
		catch (Exception e) {
			throw new RuntimeException("could not create twitter account to twitter account relationship", e);
		}
	}

	/** @see ie.ucd.forlang.neo4j.GraphManager#listEmailAccounts() */
	@Override
	public final List<EmailAccount> listEmailAccounts() {
		try {
			return Utils.toEmailAccountList(this,
					getAllNodesByLabel(DynamicLabel.label(GraphObjectType.EmailAccount.toString())));
		}
		catch (Exception e) {
			throw new RuntimeException("could not retrieve email account list", e);
		}
	}

	/** @see ie.ucd.forlang.neo4j.GraphManager#listPeople() */
	@Override
	public final List<Person> listPeople() throws RuntimeException {
		try {
			return Utils.toPersonList(this, getAllNodesByLabel(DynamicLabel.label(GraphObjectType.Person.toString())));
		}
		catch (Exception e) {
			throw new RuntimeException("could not retrieve person list", e);
		}
	}

	/**
	 * For testing only
	 * 
	 * @param graphDb the graphDb to set
	 */
	public final void setGraphDb(GraphDatabaseService graphDb) {
		this.graphDb = graphDb;
	}

	/**
	 * Add a <code>GraphObject</code> to the database
	 * 
	 * @param object GraphObject The object to add. Cannot be <code>null</code>
	 * @return Node The newly created <code>Node</code> object
	 * @throws RuntimeException If the object could not be added
	 */
	private final Node addNode(GraphObject object) throws RuntimeException {
		Validate.notNull(object, "object cannot be null");
		Node node = null;
		Iterator<Entry<String, Object>> props = null;
		Entry<String, Object> entry = null;
		try {
			node = find(object);
			if (node == null) {
				try (Transaction tx = graphDb.beginTx()) {
					node = graphDb.createNode(DynamicLabel.label(object.getGraphObjectType().toString()));
					props = object.getPropertiesIterator();
					while (props.hasNext()) {
						entry = props.next();
						node.setProperty(entry.getKey(), entry.getValue());
						entry = null;
					}
					tx.success();
				}
			}
			return node;
		}
		catch (Exception e) {
			throw new RuntimeException("could not add node: " + node.toString(), e);
		}
		finally {
			node = null;
			props = null;
			entry = null;
		}
	}

	/**
	 * Create a relationship between two objects
	 * 
	 * @param obj1 GraphObject The starting point of the relationship. Cannot be <code>null</code>
	 * @param obj2 GraphObject The ending point of the relationship. Cannot be <code>null</code>
	 * @param type RelationshipType The type of relationship. Cannot be <code>null</code>
	 * @return Relationship The formed relationship object
	 * @throws RuntimeException If the relationship could not be created
	 */
	private final Relationship createRelationship(GraphObject start, GraphObject end, RelationshipType type)
			throws RuntimeException {
		Validate.notNull(start, "start object cannot be null");
		Validate.notNull(end, "end object cannot be null");
		Validate.notNull(type, "relationship type cannot be null");
		Node node1 = null, node2 = null;
		Relationship rel = null;
		try {
			node1 = addNode(start);
			node2 = addNode(end);
			try (Transaction tx = graphDb.beginTx()) {
				rel = node1.createRelationshipTo(node2, type);
				tx.success();
			}
			return rel;
		}
		catch (Exception e) {
			throw new RuntimeException("could not create relationship link", e);
		}
		finally {
			node1 = null;
			node2 = null;
			rel = null;
		}
	}

	/**
	 * Find an object that already exists in the graph database (to try and avoid duplicate entries)
	 * 
	 * @param obj GraphObject The object type to search for. Cannot be <code>null</code>
	 * @return Node The matched object if found, or <code>null</code> if not matched
	 * @throws RuntimeException If the search fails
	 */
	private final Node find(GraphObject obj) throws RuntimeException {
		Validate.notNull(obj, "lookup object cannot be null");
		Validate.notEmpty(obj.getPrimaryPropertyName(), "primary property name cannot be null");
		Validate.notNull(obj.getPrimaryPropertyValue(), "primary property value cannot be null");
		Label label = DynamicLabel.label(obj.getGraphObjectType().toString().toString());
		Node node = null;
		try (Transaction tx = graphDb.beginTx()) {
			try (ResourceIterator<Node> nodes = graphDb.findNodesByLabelAndProperty(label,
					obj.getPrimaryPropertyName(), obj.getPrimaryPropertyValue()).iterator()) {
				if (nodes.hasNext()) {
					node = nodes.next();
				}
			}
			tx.success();
			return node;
		}
		catch (Exception e) {
			throw new RuntimeException("could not find node: " + label.toString(), e);
		}
		finally {
			label = null;
			node = null;
		}
	}

	/**
	 * Get a list of all nodes in the graph database by <code>Label</code>
	 * 
	 * @param label Label The label type to return. Cannot be <code>null</code>
	 * @return List<Node> A list of all nodes of the specified type or an empty list if none are found
	 * @throws RuntimeException If the noe list could not be retrieved
	 */
	private final List<Node> getAllNodesByLabel(Label label) throws RuntimeException {
		Validate.notNull(label, "label cannot be null");
		List<Node> list = new ArrayList<Node>();
		try (Transaction tx = graphDb.beginTx()) {
			try (ResourceIterator<Node> nodes = GlobalGraphOperations.at(graphDb).getAllNodesWithLabel(label)
					.iterator()) {
				while (nodes.hasNext()) {
					list.add(nodes.next());
				}
			}
			tx.success();
			return list;
		}
		catch (Exception e) {
			throw new RuntimeException("could not get all nodes: " + label.toString(), e);
		}
		finally {
			label = null;
			list = null;
		}
	}
}