package ie.ucd.forlang.neo4j;

import ie.ucd.forlang.neo4j.object.EmailAccount;
import ie.ucd.forlang.neo4j.object.EmailMessage;
import ie.ucd.forlang.neo4j.object.Person;

import java.io.File;
import java.util.List;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/** Interface to allow access to a graph database manager of autopsy/OSI purposes */
public interface GraphManager {

	/**
	 * Add an <code>EmailAccount</code> object to the graph database. Assumes it has not already been added.
	 * 
	 * @param account EmailAccount The account to add. Cannot be <code>null</code>
	 * @return Node The added node
	 * @throws RuntimeException If the email account could not be added
	 */
	public Node addEmailAccount(EmailAccount account) throws RuntimeException;

	/**
	 * Add an <code>EmailMessage</code> object to the graph database. Assumes it has not already been added.
	 * 
	 * @param msg EmailMessage The message to add. Cannot be <code>null</code>
	 * @return Node The added node
	 * @throws RuntimeException If the email message could not be added
	 */
	public Node addEmailMessage(EmailMessage msg) throws RuntimeException;

	/**
	 * Add an <code>Person</code> object to the graph database. Assumes it has not already been added.
	 * 
	 * @param person Person The person to add. Cannot be <code>null</code>
	 * @return Node The added node
	 * @throws RuntimeException If the person could not be added
	 */
	public Node addPerson(Person person) throws RuntimeException;

	public void destroy() throws RuntimeException;

	public GraphDatabaseService getGraphDatabaseService();

	public void init(File dbRoot) throws RuntimeException;

	public Relationship linkEmailChain(EmailMessage msg, EmailAccount from, List<EmailAccount> to)
			throws RuntimeException;

	/**
	 * Create a relationship between two <code>Person</code> objects in the graph database. Assumes that they have not
	 * already been added.
	 * 
	 * @param person Person Person A. Cannot be <code>null</code>
	 * @param knows Person Who knows Person B. Cannot be <code>null</code>
	 * @return Relationship The created <code>Relationship</code> object
	 * @throws RuntimeException If the relationship could not be created
	 */
	public Relationship linkPerson(Person person, Person knows) throws RuntimeException;

	/**
	 * Create a relationship between a <code>Person</code> and an <code>EmailAccount</code> objects in the graph
	 * database. Assumes that they have not already been added.
	 * 
	 * @param person Person The person that owns the email account. Cannot be <code>null</code>
	 * @param account EmailAccount The account that is owned. Cannot be <code>null</code>
	 * @return Relationship The created <code>Relationship</code> object
	 * @throws RuntimeException If the relationship could not be created
	 */
	public Relationship linkPersonToEmailAccount(Person person, EmailAccount account) throws RuntimeException;

	/**
	 * Get a <code>List</code> of all of the <code>EmailAccount</code>s in the graph database
	 * 
	 * @return List<EmailAccount> The list of all of the email accounts
	 * @throws RuntimeException If the list could not be retrieved
	 */
	public List<EmailAccount> listEmailAccounts() throws RuntimeException;

	/**
	 * Get a <code>List</code> of all of the <code>Person</code>s in the graph database
	 * 
	 * @return List<Person> The list of all of the people
	 * @throws RuntimeException If the list could not be retrieved
	 */
	public List<Person> listPeople() throws RuntimeException;
}