package ie.ucd.forlang.neo4j.object;

import java.util.Date;

public interface EmailMessage extends GraphObject {

	public Date getDateSent();

	public String[] getRecipients();

	public String getSender();

	public String getSubject();

	public void setDateSent(Date dateSent);

	public void setRecipients(String[] recipients);

	public void setSender(String sender);

	public void setSubject(String subject);
}