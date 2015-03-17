package ie.ucd.forlang.neo4j;

import ie.ucd.forlang.neo4j.object.EmailAccount;
import ie.ucd.forlang.neo4j.object.EmailAccountImpl;
import ie.ucd.forlang.neo4j.object.Person;
import ie.ucd.forlang.neo4j.object.PersonImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.lang.Validate;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/** General utility methods */
public final class Utils {

	/**
	 * Convert a list of <code>Node</code> objects to a list of <code>EmailAccount</code> objects
	 * 
	 * @param mgr GraphManager Needed to create transactions
	 * @param nodes List<Node> The list to convert. Cannot be null
	 * @return List<Person> The converted list
	 * @throws RuntimeException If the list could not be converted
	 */
	public static final List<EmailAccount> toEmailAccountList(GraphManager mgr, List<Node> nodes)
			throws RuntimeException {
		Validate.notNull(nodes, "nodes list cannot be null");
		List<EmailAccount> emails = new ArrayList<EmailAccount>();
		ListIterator<Node> nodesIt = nodes.listIterator();
		try (Transaction tx = mgr.getGraphDatabaseService().beginTx()) {
			while (nodesIt.hasNext()) {
				emails.add(new EmailAccountImpl(nodesIt.next()));
			}
			tx.success();
		}
		return emails;
	}

	/**
	 * Convert a list of <code>Node</code> objects to a list of <code>Person</code> objects
	 * 
	 * @param mgr GraphManager Needed to create transactions
	 * @param nodes List<Node> The list to convert. Cannot be null
	 * @return List<Person> The converted list
	 * @throws RuntimeException If the list could not be converted
	 */
	public static final List<Person> toPersonList(GraphManager mgr, List<Node> nodes) throws RuntimeException {
		Validate.notNull(nodes, "nodes list cannot be null");
		List<Person> people = new ArrayList<Person>();
		ListIterator<Node> nodesIt = nodes.listIterator();
		try (Transaction tx = mgr.getGraphDatabaseService().beginTx()) {
			while (nodesIt.hasNext()) {
				people.add(new PersonImpl(nodesIt.next()));
			}
			tx.success();
		}
		return people;
	}
	// TODO fix later
	// private List<GraphObject> convertList(List<Node> nodes, Class target) {
	// Validate.notNull(nodes, "nodes list cannot be null");
	// List<GraphObject> people = new ArrayList<GraphObject>();
	// ListIterator<Node> nodesIt = nodes.listIterator();
	// while (nodesIt.hasNext()) {
	// target.getConstructor(Node.class);
	// people.add(new PersonImpl(nodesIt.next()));
	// }
	// return people;
	// }
}