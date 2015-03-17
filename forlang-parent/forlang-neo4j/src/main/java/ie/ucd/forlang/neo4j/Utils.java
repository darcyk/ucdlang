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

/** General utility methods */
public final class Utils {

	/**
	 * Convert a list of <code>Node</code> objects to a list of <code>EmailAccount</code> objects
	 * 
	 * @param nodes List<Node> The list to convert. Cannot be null
	 * @return List<Person> The converted list
	 * @throws RuntimeException If the list could not be converted
	 */
	public static final List<EmailAccount> toEmailAccountList(List<Node> nodes) throws RuntimeException {
		Validate.notNull(nodes, "nodes list cannot be null");
		List<EmailAccount> emails = new ArrayList<EmailAccount>();
		ListIterator<Node> nodesIt = nodes.listIterator();
		while (nodesIt.hasNext()) {
			emails.add(new EmailAccountImpl(nodesIt.next()));
		}
		return emails;
	}

	/**
	 * Convert a list of <code>Node</code> objects to a list of <code>Person</code> objects
	 * 
	 * @param nodes List<Node> The list to convert. Cannot be null
	 * @return List<Person> The converted list
	 * @throws RuntimeException If the list could not be converted
	 */
	public static final List<Person> toPersonList(List<Node> nodes) throws RuntimeException {
		Validate.notNull(nodes, "nodes list cannot be null");
		List<Person> people = new ArrayList<Person>();
		ListIterator<Node> nodesIt = nodes.listIterator();
		while (nodesIt.hasNext()) {
			people.add(new PersonImpl(nodesIt.next()));
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