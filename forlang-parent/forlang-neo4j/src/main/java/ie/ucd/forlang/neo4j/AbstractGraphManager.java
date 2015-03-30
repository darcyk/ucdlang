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
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.tooling.GlobalGraphOperations;

public abstract class AbstractGraphManager implements GraphManager {

	protected GraphDatabaseService graphDb = null;

	public AbstractGraphManager() {
		super();
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

	/** @see GraphManager#linkEmailChain(EmailMessage, EmailAccount, List) */
	@Override
	public final List<Relationship> linkEmailChain(EmailMessage msg, EmailAccount from, List<EmailAccount> to)
			throws RuntimeException {
		Validate.notNull(msg);
		Validate.notNull(from);
		Validate.notNull(to);
		Validate.noNullElements(to);
		List<Relationship> rels = null;
		try {
			rels = new ArrayList<Relationship>(to.size() + 1);
			rels.add(createRelationship(from, msg, RelationshipType.SENT));
			for (EmailAccount acc : to) {
				rels.add(createRelationship(msg, acc, RelationshipType.RECEIVED));
				acc = null;
			}
			return rels;
		}
		catch (Exception e) {
			throw new RuntimeException("could not create person to person relationship", e);
		}
		finally {
			rels = null;
		}
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
	@Deprecated
	public final Relationship linkPersonToTwitterAccount(Person person, TwitterAccount account) throws RuntimeException {
		try {
			return createRelationship(person, account, RelationshipType.PROBABLY_OWNS);
		}
		catch (Exception e) {
			throw new RuntimeException("could not create person to twitter account relationship", e);
		}
	}

	/** @see GraphManager#linkPersonToTwitterAccount(Person, TwitterAccount, RelationshipType) */
	@Override
	public final Relationship linkPersonToTwitterAccount(Person person, TwitterAccount account, RelationshipType type)
			throws RuntimeException {
		try {
			Validate.isTrue(RelationshipType.OWNS.equals(type) || RelationshipType.PROBABLY_OWNS.equals(type),
					"illegal relationship type: " + type);
			return createRelationship(person, account, type);
		}
		catch (Exception e) {
			throw new RuntimeException("could not create person to twitter account relationship", e);
		}
	}

	/** @see GraphManager#linkTwitterAccounts(TwitterAccount, TwitterAccount) */
	@Override
	@Deprecated
	public final Relationship linkTwitterAccounts(TwitterAccount follower, TwitterAccount follows)
			throws RuntimeException {
		try {
			return createRelationship(follower, follows, RelationshipType.FOLLOWS);
		}
		catch (Exception e) {
			throw new RuntimeException("could not create twitter account to twitter account relationship", e);
		}
	}

	/** @see GraphManager#linkTwitterAccounts(TwitterAccount, TwitterAccount, RelationshipType) */
	@Override
	public final Relationship linkTwitterAccounts(TwitterAccount follower, TwitterAccount follows, RelationshipType type)
			throws RuntimeException {
		try {
			Validate.isTrue(RelationshipType.FOLLOWS.equals(type) || RelationshipType.IS_FOLLOWED_BY.equals(type),
					"illegal relationship type: " + type);
			return createRelationship(follower, follows, type);
		}
		catch (Exception e) {
			throw new RuntimeException("could not create twitter account to twitter account relationship", e);
		}
	}

	/** @see ie.ucd.forlang.neo4j.GraphManager#listEmailAccounts() */
	@Override
	public final List<EmailAccount> listEmailAccounts() {
		try {
			return toEmailAccountList(getAllNodesByLabel(DynamicLabel.label(GraphObjectType.EmailAccount.toString())));
		}
		catch (Exception e) {
			throw new RuntimeException("could not retrieve email account list", e);
		}
	}

	/** @see ie.ucd.forlang.neo4j.GraphManager#listPeople() */
	@Override
	public final List<Person> listPeople() throws RuntimeException {
		try {
			return toPersonList(getAllNodesByLabel(DynamicLabel.label(GraphObjectType.Person.toString())));
		}
		catch (Exception e) {
			throw new RuntimeException("could not retrieve person list", e);
		}
	}

	/** @see GraphManager#listTwitterAccounts() */
	@Override
	public final List<TwitterAccount> listTwitterAccounts() throws RuntimeException {
		try {
			return toTwitterAccountList(getAllNodesByLabel(DynamicLabel
					.label(GraphObjectType.TwitterAccount.toString())));
		}
		catch (Exception e) {
			throw new RuntimeException("could not retrieve twitter account list", e);
		}
	}

	/** @see GraphManager#setGraphDatabaseService(GraphDatabaseService) */
	@Override
	public final void setGraphDatabaseService(GraphDatabaseService graphDb) {
		this.graphDb = graphDb;
	}

	/** @see AbstractGraphManager#addNode(GraphObject) */
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

	/** @see AbstractGraphManager#createRelationship(GraphObject, GraphObject, RelationshipType) */
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

	/** @see AbstractGraphManager#find(GraphObject) */
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

	/** @see AbstractGraphManager#getAllNodesByLabel(Label) */
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

	/** @see AbstractGraphManager#toEmailAccountList(List) */
	private final List<EmailAccount> toEmailAccountList(List<Node> nodes) throws RuntimeException {
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
	private final List<Person> toPersonList(List<Node> nodes) throws RuntimeException {
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
	private final List<TwitterAccount> toTwitterAccountList(List<Node> nodes) {
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