package ie.ucd.forlang.neo4j.object;

import java.util.Date;

/**
 * Object to encapsulate an email message
 * 
 * @author Kev D'Arcy
 */
public interface EmailMessage extends GraphObject {

	/**
	 * Get the date the email message was sent
	 * 
	 * @return Date The email message was sent
	 */
	public Date getDateSent();

	/**
	 * Get the recipients for the email message
	 * 
	 * @return String[] The recipients for the email message
	 */
	public String[] getRecipients();

	/**
	 * Get the sender of the email message
	 * 
	 * @return String The sender of the email message
	 */
	public String getSender();

	/**
	 * Get the subject of the email message
	 * 
	 * @return String The subject of the email message
	 */
	public String getSubject();

	/**
	 * get the uid for this message
	 * 
	 * @return String The uid value
	 */
	public String getUid();

	/**
	 * Set the date the email message was sent
	 * 
	 * @param dateSent Date The email message was sent. Cannot be <code>null</code>
	 */
	public void setDateSent(Date dateSent);

	/**
	 * Set the recipients of the email message
	 * 
	 * @param recipients String[] The recipients of the email message. Cannot be <code>null</code> or have empty
	 *            elements
	 */
	public void setRecipients(String[] recipients);

	/**
	 * Set the sender of the email message
	 * 
	 * @param sender String The sender of the email message. Cannot be <code>null</code> or an empty string
	 */
	public void setSender(String sender);

	/**
	 * Set the subject of the email message
	 * 
	 * @param subject String The subject of the email message. Cannot be <code>null</code> or an empty string
	 */
	public void setSubject(String subject);

	/**
	 * Set the unique identified for this email message
	 * 
	 * @param uid String The UID value
	 */
	public void setUid(String uid);
}