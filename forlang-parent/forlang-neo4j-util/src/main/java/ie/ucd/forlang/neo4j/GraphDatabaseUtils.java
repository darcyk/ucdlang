package ie.ucd.forlang.neo4j;

import ie.ucd.forlang.neo4j.object.EmailAccount;
import ie.ucd.forlang.neo4j.object.EmailAccountImpl;
import ie.ucd.forlang.neo4j.object.EmailMessage;
import ie.ucd.forlang.neo4j.object.GraphObject;
import ie.ucd.forlang.neo4j.object.GraphObjectType;
import ie.ucd.forlang.neo4j.object.Person;
import ie.ucd.forlang.neo4j.object.PersonImpl;
import ie.ucd.forlang.neo4j.object.RelationshipType;
import ie.ucd.forlang.neo4j.object.TwitterAccount;
import ie.ucd.forlang.neo4j.object.TwitterAccountImpl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;

import org.apache.commons.lang3.Validate;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;

/**
 * Selection of static methods to provide utility functions for {@code GraphDatabaseService} objects.
 * 
 * @author Kev D'Arcy
 */
public final class GraphDatabaseUtils {

	/**
	 * Add an {@code EmailAccount} object to the graph database. Assumes it has not already been added.
	 *
	 * @param graphDb GraphDatabaseService The database connection object
	 * @param account EmailAccount The account to add. Cannot be {@code null}
	 * @return Node The added node
	 * @throws RuntimeException If the email account could not be added
	 */
	public static final Node addEmailAccount(GraphDatabaseService graphDb, EmailAccount account)
			throws RuntimeException {
		try {
			return addNode(graphDb, account);
		}
		catch (Exception e) {
			throw new RuntimeException("could not add email account to graph database", e);
		}
	}

	/**
	 * Add an {@code EmailMessage} object to the graph database. Assumes it has not already been added.
	 *
	 * @param graphDb GraphDatabaseService The database connection object
	 * @param msg EmailMessage The message to add. Cannot be {@code null}
	 * @return Node The added node
	 * @throws RuntimeException If the email message could not be added
	 */
	public static final Node addEmailMessage(GraphDatabaseService graphDb, EmailMessage msg) throws RuntimeException {
		try {
			return addNode(graphDb, msg);
		}
		catch (Exception e) {
			throw new RuntimeException("could not add email message to graph database", e);
		}
	}

	/**
	 * Add an {@code Person} object to the graph database. Assumes it has not already been added.
	 *
	 * @param graphDb GraphDatabaseService The database connection object
	 * @param person Person The person to add. Cannot be {@code null}
	 * @return Node The added node
	 * @throws RuntimeException If the person could not be added
	 */
	public static final Node addPerson(GraphDatabaseService graphDb, Person person) throws RuntimeException {
		try {
			return addNode(graphDb, person);
		}
		catch (Exception e) {
			throw new RuntimeException("could not add person to graph database", e);
		}
	}

	/**
	 * Add a {@codeTwitterAccount} object to the graph database. Assumes it has not already been added.
	 *
	 * @param graphDb GraphDatabaseService The database connection object
	 * @param twitterAccount TwitterAccount The twitter account to add. Cannot be {@code null}
	 * @return Node The added node
	 * @throws RuntimeException If the person could not be added
	 */
	public static final Node addTwitterAccount(GraphDatabaseService graphDb, TwitterAccount account)
			throws RuntimeException {
		try {
			return addNode(graphDb, account);
		}
		catch (Exception e) {
			throw new RuntimeException("could not add twitter account to graph database", e);
		}
	}

	/**
	 * Link and {@code EmailMessage} to the sending and receiving {@code EmailAccount}s. Returns at least 2 relationship
	 * objects between the sender and the mail object,and the recipient(s) and the mail object
	 *
	 * @param graphDb GraphDatabaseService The database connection object
	 * @param msg EmailMessage The email message object. Cannot be {@code null}
	 * @param from EmailAccount The email senders account. Cannot be {@code null}
	 * @param to List<EmailAccount> The (one or more) recipients. Cannot be {@code null} or have {@code null} values
	 * @return List<Relationship> The list of created relationships. The senders relationship will always be first in
	 *         the list
	 * @throws RuntimeException
	 */
	public static final List<Relationship> linkEmailChain(GraphDatabaseService graphDb, EmailMessage msg,
			EmailAccount from, List<EmailAccount> to) throws RuntimeException {
		Validate.notNull(msg);
		Validate.notNull(from);
		Validate.notNull(to);
		Validate.noNullElements(to);
		List<Relationship> rels = null;
		try {
			rels = new ArrayList<Relationship>(to.size() + 1);
			rels.add(createRelationship(graphDb, from, msg, RelationshipType.SENT));
			for (EmailAccount acc : to) {
				rels.add(createRelationship(graphDb, msg, acc, RelationshipType.RECEIVED));
				acc = null;
			}
			return rels;
		}
		catch (Exception e) {
			throw new RuntimeException("could not create email chain relationship", e);
		}
		finally {
			rels = null;
		}
	}

	/**
	 * Create a relationship between two {@code Person} objects in the graph database. Assumes that they have not
	 * already been added.
	 *
	 * @param graphDb GraphDatabaseService The database connection object
	 * @param person Person Person A. Cannot be {@code null}
	 * @param knows Person Who knows Person B. Cannot be {@code null}
	 * @return Relationship The created {@code Relationship} object
	 * @throws RuntimeException If the relationship could not be created
	 * @see RelationshipType.KNOWS
	 */
	public static final Relationship linkPersons(GraphDatabaseService graphDb, Person person, Person knows)
			throws RuntimeException {
		try {
			return createRelationship(graphDb, person, knows, RelationshipType.KNOWNS);
		}
		catch (Exception e) {
			throw new RuntimeException("could not create person to person relationship", e);
		}
	}

	/**
	 * Create a relationship between a {@code Person} and an {@code EmailAccount} objects in the graph database. Assumes
	 * that they have not already been added.
	 *
	 * @param graphDb GraphDatabaseService The database connection object
	 * @param person Person The person that owns the email account. Cannot be {@code null}
	 * @param account EmailAccount The account that is owned. Cannot be {@code null}
	 * @return Relationship The created {@code Relationship} object
	 * @throws RuntimeException If the relationship could not be created
	 * @see RelationshipType.OWNS
	 */
	public static final Relationship linkPersonToEmailAccount(GraphDatabaseService graphDb, Person person,
			EmailAccount account) throws RuntimeException {
		try {
			return createRelationship(graphDb, person, account, RelationshipType.OWNS);
		}
		catch (Exception e) {
			throw new RuntimeException("could not create person to email account relationship", e);
		}
	}

	/**
	 * Creates a relationship between a {@code Person} and a {@codeTwitterAccount} object. The following
	 * {@code RelationshipType}s are only permitted:
	 * <ul>
	 * <li>{@link RelationshipType.OWNS}</li>
	 * <li>{@link RelationshipType.PROBABLY_OWNS}</li>
	 *
	 * @param graphDb GraphDatabaseService The database connection object
	 * @param follower TwitterAccount The "follower" twitter account
	 * @param follows TwitterAccount The "being followed" twitter account
	 * @param type RelationshipType The type of relationship between the {@code Person} and the {@codeTwitterAccount}
	 * @return Relationship The created {@code Relationship} object
	 * @throws RuntimeException If the relationship could not be created
	 * @see RelationshipType
	 */
	public static final Relationship linkPersonToTwitterAccount(GraphDatabaseService graphDb, Person person,
			TwitterAccount account, RelationshipType type) throws RuntimeException {
		try {
			Validate.isTrue(RelationshipType.OWNS.equals(type) || RelationshipType.PROBABLY_OWNS.equals(type),
					"illegal relationship type: " + type);
			return createRelationship(graphDb, person, account, type);
		}
		catch (Exception e) {
			throw new RuntimeException("could not create person to twitter account relationship", e);
		}
	}

	/**
	 * Creates a relationship between two {@codeTwitterAccount} object. The following {@code RelationshipType}s are only
	 * permitted:
	 * <ul>
	 * <li>{@link RelationshipType.FOLLOWING}</li>
	 * <li>{@link RelationshipType.IS_FOLLOWED_BY}</li>
	 *
	 * @param graphDb GraphDatabaseService The database connection object
	 * @param follower TwitterAccount The source twitter account
	 * @param follows TwitterAccount The destination twitter account
	 * @param type RelationshipType The type of relationship between the two {@codeTwitterAccount}s
	 * @return Relationship The created {@code Relationship} object
	 * @throws RuntimeException If the relationship could not be created
	 */
	public static final Relationship linkTwitterAccounts(GraphDatabaseService graphDb, TwitterAccount follower,
			TwitterAccount follows, RelationshipType type) throws RuntimeException {
		try {
			Validate.isTrue(RelationshipType.FOLLOWS.equals(type) || RelationshipType.IS_FOLLOWED_BY.equals(type),
					"illegal relationship type: " + type);
			return createRelationship(graphDb, follower, follows, type);
		}
		catch (Exception e) {
			throw new RuntimeException("could not create twitter account to twitter account relationship", e);
		}
	}

	/**
	 * Get a {@code List} of all of the {@code EmailAccount}s in the graph database
	 *
	 * @param graphDb GraphDatabaseService The database connection object
	 * @return List<EmailAccount> The list of all of the email accounts
	 * @throws RuntimeException If the list could not be retrieved
	 */
	public static final List<EmailAccount> listEmailAccounts(GraphDatabaseService graphDb) {
		try {
			return toEmailAccountList(graphDb,
					getAllNodesByLabel(graphDb, DynamicLabel.label(GraphObjectType.EmailAccount.toString())));
		}
		catch (Exception e) {
			throw new RuntimeException("could not retrieve email account list", e);
		}
	}

	/**
	 * Get a {@code List} of all of the {@code Person}s in the graph database
	 *
	 * @param graphDb GraphDatabaseService The database connection object
	 * @return List<Person> The list of all of the people
	 * @throws RuntimeException If the list could not be retrieved
	 */
	public static final List<Person> listPeople(GraphDatabaseService graphDb) throws RuntimeException {
		try {
			return toPersonList(graphDb,
					getAllNodesByLabel(graphDb, DynamicLabel.label(GraphObjectType.Person.toString())));
		}
		catch (Exception e) {
			throw new RuntimeException("could not retrieve person list", e);
		}
	}

	/**
	 * Get a {@code List} of all of the {@codeTwitterAccount}s in the graph database
	 *
	 * @param graphDb GraphDatabaseService The database connection object
	 * @return List<TwitterAccount> The list of all of the twitter accounts
	 * @throws RuntimeException If the list could not be retrieved
	 */
	public static final List<TwitterAccount> listTwitterAccounts(GraphDatabaseService graphDb) throws RuntimeException {
		try {
			return toTwitterAccountList(graphDb,
					getAllNodesByLabel(graphDb, DynamicLabel.label(GraphObjectType.TwitterAccount.toString())));
		}
		catch (Exception e) {
			throw new RuntimeException("could not retrieve twitter account list", e);
		}
	}

	/**
	 * Add a {@code GraphObject} to the database. If a node with the primary property exists, then a new node is not
	 * created, the existing node is returned.
	 * 
	 * @param graphDb GraphDatabaseService The database connection object
	 * @param object GraphObject The object to add. Cannot be {@code null}
	 * @return Node The created node
	 * @throws RuntimeException If the node could not be added
	 */
	private static final Node addNode(GraphDatabaseService graphDb, GraphObject object) throws RuntimeException {
		Validate.notNull(object, "object cannot be null");
		Node node = null;
		Iterator<Entry<String, Object>> props = null;
		Entry<String, Object> entry = null;
		try {
			node = find(graphDb, object);
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

	/** @see AbstractGraphManager#createRelationship(GraphObject, GraphObject, RelationshipType) */
	private static final Relationship createRelationship(GraphDatabaseService graphDb, GraphObject start,
			GraphObject end, RelationshipType type) throws RuntimeException {
		Validate.notNull(start, "start object cannot be null");
		Validate.notNull(end, "end object cannot be null");
		Validate.notNull(type, "relationship type cannot be null");
		Node node1 = null, node2 = null;
		Relationship rel = null;
		try {
			node1 = addNode(graphDb, start);
			node2 = addNode(graphDb, end);
			try (Transaction tx = graphDb.beginTx()) {
				for (Relationship r : node1.getRelationships(type, Direction.OUTGOING)) {
					if (r.getEndNode().getId() == node2.getId()) {
						rel = r;
						break;
					}
				}
				if (rel == null) {
					rel = node1.createRelationshipTo(node2, type);
				}
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

	/** @see AbstractGraphManager#find(GraphObject) */
	private static final Node find(GraphDatabaseService graphDb, GraphObject obj) throws RuntimeException {
		Validate.notNull(obj, "lookup object cannot be null");
		Validate.notEmpty(obj.getPrimaryPropertyName(), "primary property name cannot be null");
		Validate.notNull(obj.getPrimaryPropertyValue(), "primary property value cannot be null");
		Label label = DynamicLabel.label(obj.getGraphObjectType().toString().toString());
		Node node = null;
		try (Transaction tx = graphDb.beginTx()) {
			// try (ResourceIterator<Node> nodes = graphDb.findNodesByLabelAndProperty(label,
			// obj.getPrimaryPropertyName(), obj.getPrimaryPropertyValue()).iterator()) {
			try (ResourceIterator<Node> nodes = graphDb.findNodes(label, obj.getPrimaryPropertyName(),
					obj.getPrimaryPropertyValue())) {
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

	/** @see AbstractGraphManager#getAllNodesByLabel(Label) */
	private static final List<Node> getAllNodesByLabel(GraphDatabaseService graphDb, Label label)
			throws RuntimeException {
		Validate.notNull(label, "label cannot be null");
		List<Node> list = new ArrayList<Node>();
		try (Transaction tx = graphDb.beginTx()) {
			// try (ResourceIterator<Node> nodes =
			// GlobalGraphOperations.at(graphDb).getAllNodesWithLabel(label).iterator()) {
			try (ResourceIterator<Node> nodes = graphDb.findNodes(label)) {
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

	/** @see AbstractGraphManager#toEmailAccountList(List) */
	private static final List<EmailAccount> toEmailAccountList(GraphDatabaseService graphDb, List<Node> nodes)
			throws RuntimeException {
		Validate.notNull(nodes, "nodes list cannot be null");
		List<EmailAccount> emails = new ArrayList<EmailAccount>();
		ListIterator<Node> nodesIt = nodes.listIterator();
		try (Transaction tx = graphDb.beginTx()) {
			while (nodesIt.hasNext()) {
				emails.add(new EmailAccountImpl(nodesIt.next()));
			}
			tx.success();
		}
		return emails;
	}

	/** @see AbstractGraphManager#toPersonList(List) */
	private static final List<Person> toPersonList(GraphDatabaseService graphDb, List<Node> nodes)
			throws RuntimeException {
		Validate.notNull(nodes, "nodes list cannot be null");
		List<Person> people = new ArrayList<Person>();
		ListIterator<Node> nodesIt = nodes.listIterator();
		try (Transaction tx = graphDb.beginTx()) {
			while (nodesIt.hasNext()) {
				people.add(new PersonImpl(nodesIt.next()));
			}
			tx.success();
		}
		return people;
	}

	/** @see AbstractGraphManager#toTwitterAccountList(List) */
	private static final List<TwitterAccount> toTwitterAccountList(GraphDatabaseService graphDb, List<Node> nodes) {
		Validate.notNull(nodes, "nodes list cannot be null");
		List<TwitterAccount> accounts = new ArrayList<TwitterAccount>();
		ListIterator<Node> nodesIt = nodes.listIterator();
		try (Transaction tx = graphDb.beginTx()) {
			while (nodesIt.hasNext()) {
				accounts.add(new TwitterAccountImpl(nodesIt.next()));
			}
			tx.success();
		}
		return accounts;
	}
}