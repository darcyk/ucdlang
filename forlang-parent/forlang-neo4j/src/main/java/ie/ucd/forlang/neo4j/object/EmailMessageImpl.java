package ie.ucd.forlang.neo4j.object;

import ie.ucd.forlang.neo4j.Constants;

import java.util.Date;
import java.util.List;

public final class EmailMessageImpl extends GraphObjectImpl implements EmailMessage {

	public EmailMessageImpl() {
		super();
	}

	public EmailMessageImpl(EmailAccount sender, List<EmailAccount> recipients, String subject, Date dateSent) {
		super();
		setSender(sender);
		setRecipientList(recipients);
		setSubject(subject);
		setDateSent(dateSent);
	}

	/** @see GraphObject#getGraphObjectType() */
	@Override
	public final GraphObjectType getGraphObjectType() {
		return GraphObjectType.EmailMessage;
	}

	/** @see EmailMessage#getDateSent() */
	@Override
	public final Date getDateSent() {
		return (Date) getProperty(Constants.PROP_DATE_SENT);
	}

	/** @see EmailMessage#getRecipientList() */
	@Override
	@SuppressWarnings("unchecked")
	public final List<EmailAccount> getRecipientList() {
		return (List<EmailAccount>) getProperty(Constants.PROP_RECIPIENT_LIST);
	}

	/** @see EmailMessage#getSender() */
	@Override
	public final EmailAccount getSender() {
		return (EmailAccount) getProperty(Constants.PROP_SENDER);
	}

	/** @see EmailMessage#getSubject() */
	@Override
	public final String getSubject() {
		return (String) getProperty(Constants.PROP_SUBJECT);
	}

	/** @see EmailMessage#setDateSent(Date) */
	@Override
	public final void setDateSent(Date dateSent) {
		setProperty(Constants.PROP_DATE_SENT, dateSent);
	}

	/** @see EmailMessage#setRecipientList(List) */
	@Override
	public final void setRecipientList(List<EmailAccount> recipients) {
		setProperty(Constants.PROP_RECIPIENT_LIST, recipients);
	}

	/** @see EmailMessage#setSender(EmailAccount) */
	@Override
	public final void setSender(EmailAccount sender) {
		setProperty(Constants.PROP_SENDER, sender);
	}

	/** @see EmailMessage#setSubject(String) */
	@Override
	public final void setSubject(String subject) {
		setProperty(Constants.PROP_SUBJECT, subject);
	}
}