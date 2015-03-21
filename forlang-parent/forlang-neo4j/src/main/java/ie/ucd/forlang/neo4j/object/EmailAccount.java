package ie.ucd.forlang.neo4j.object;

/**
 * Object to encapsulate an email account
 * 
 * @author Kev D'Arcy
 */
public interface EmailAccount extends GraphObject {

	/**
	 * Get the email address associated with this account
	 * 
	 * @return String The email address
	 */
	public String getEmailAddress();

	/**
	 * Set the email address associated with this account
	 * 
	 * @param emailAddress String The email address. Cannot be <code>null</code> or an empty string
	 */
	public void setEmailAddress(String emailAddress);
}