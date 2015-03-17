package ie.ucd.forlang.neo4j.object;

import java.util.Date;
import java.util.List;

public interface EmailMessage extends GraphObject {

	public Date getDateSent();

	public List<EmailAccount> getRecipients();

	public EmailAccount getSender();

	public String getSubject();

	public void setDateSent(Date dateSent);

	public void setRecipients(List<EmailAccount> recipients);

	public void setSender(EmailAccount sender);

	public void setSubject(String subject);
}