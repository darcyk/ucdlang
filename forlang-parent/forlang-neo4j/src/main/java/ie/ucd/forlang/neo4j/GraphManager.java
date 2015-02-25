package ie.ucd.forlang.neo4j;

import java.util.List;

public interface GraphManager {
	
	public void addPerson(Person person) throws IllegalArgumentException;
	
	public void linkPerson(Person person, Person knows) throws IllegalArgumentException;
	
	public void addAccount(Account account) throws IllegalArgumentException;
	
	public void linkAccount(Person person, Account account) throws IllegalArgumentException;
	
	public void addMessage(Message msg, Account from, List<Account> to) throws IllegalArgumentException;
}