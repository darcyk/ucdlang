package ie.ucd.forlang.neo4j.object;

import ie.ucd.forlang.neo4j.Constants;

import java.util.Date;

import org.apache.commons.lang.Validate;

public final class EmailMessageImpl extends GraphObjectImpl implements EmailMessage {

	public EmailMessageImpl() {
		super();
	}

	public EmailMessageImpl(String sender, String[] recipients, String subject, Date dateSent) {
		super();
		setSender(sender);
		setRecipients(recipients);
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
		return new Date((Long) getProperty(Constants.PROP_DATE_SENT));
	}

	/** @see EmailMessage#getRecipients() */
	@Override
	public final String[] getRecipients() {
		return (String[]) getProperty(Constants.PROP_RECIPIENTS);
	}

	/** @see EmailMessage#getSender() */
	@Override
	public final String getSender() {
		return (String) getProperty(Constants.PROP_SENDER);
	}

	/** @see EmailMessage#getSubject() */
	@Override
	public final String getSubject() {
		return (String) getProperty(Constants.PROP_SUBJECT);
	}

	/** @see EmailMessage#setDateSent(Date) */
	@Override
	public final void setDateSent(Date dateSent) {
		Validate.notNull(dateSent, "date sent cannot be null");
		setProperty(Constants.PROP_DATE_SENT, dateSent.getTime());
	}

	/** @see EmailMessage#setRecipients(String[]) */
	@Override
	public final void setRecipients(String[] recipients) {
		Validate.notNull(recipients, "recipients cannot be null");
		Validate.noNullElements(recipients, "recipients cannot have null values");
		Validate.notEmpty(recipients, "recipients must have a value");
		setProperty(Constants.PROP_RECIPIENTS, recipients);
	}

	/** @see EmailMessage#setSender(EmailAccount) */
	@Override
	public final void setSender(String sender) {
		Validate.notNull(sender, "sender cannot be null");
		Validate.notEmpty(sender, "sender must have a value");
		setProperty(Constants.PROP_SENDER, sender);
	}

	/** @see EmailMessage#setSubject(String) */
	@Override
	public final void setSubject(String subject) {
		Validate.notNull(subject, "subject cannot be null");
		Validate.notEmpty(subject, "subject must have a value");
		setProperty(Constants.PROP_SUBJECT, subject);
	}
}