package ie.ucd.forlang.neo4j;

import ie.ucd.forlang.neo4j.object.EmailAccount;
import ie.ucd.forlang.neo4j.object.EmailMessage;
import ie.ucd.forlang.neo4j.object.Person;

import java.util.List;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

public interface GraphManager {
	
	public Node addEmailAccount(EmailAccount account) throws RuntimeException;

	public Node addEmailMessage(EmailMessage msg, EmailAccount from, List<EmailAccount> to) throws RuntimeException;

	public Node addPerson(Person person) throws RuntimeException;

	public void destroy() throws RuntimeException;

	public void init() throws RuntimeException;

	public Relationship linkPerson(Person person, Person knows) throws RuntimeException;

	public Relationship linkPersonToEmailAccount(Person person, EmailAccount account) throws RuntimeException;

	public List<EmailAccount> listEmailAccounts() throws RuntimeException;

	public List<Person> listPeople() throws RuntimeException;
}