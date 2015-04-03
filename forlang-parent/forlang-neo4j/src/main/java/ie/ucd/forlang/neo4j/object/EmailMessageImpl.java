package ie.ucd.forlang.neo4j.object;

import ie.ucd.forlang.neo4j.Constants;

import java.util.Date;

import org.apache.commons.lang.Validate;

public final class EmailMessageImpl extends GraphObjectImpl implements EmailMessage {

	public EmailMessageImpl() {
		super();
	}

	public EmailMessageImpl(String uid, String sender, String[] recipients, String subject, Date dateSent) {
		super();
		setUid(uid);
		setSender(sender);
		setRecipients(recipients);
		setSubject(subject);
		setDateSent(dateSent);
	}

	/** @see EmailMessage#getDateSent() */
	@Override
	public final Date getDateSent() {
		return new Date((Long) getProperty(Constants.PROP_MAIL_DATE));
	}

	/** @see GraphObject#getGraphObjectType() */
	@Override
	public final GraphObjectType getGraphObjectType() {
		return GraphObjectType.EmailMessage;
	}

	/** @see GraphObject#getPrimaryPropertyName() */
	@Override
	public final String getPrimaryPropertyName() {
		return Constants.PROP_MAIL_UID;
	}

	/** @see GraphObject#getPrimaryPropertyValue() */
	@Override
	public final Object getPrimaryPropertyValue() {
		return getUid();
	}

	/** @see EmailMessage#getRecipients() */
	@Override
	public final String[] getRecipients() {
		return (String[]) getProperty(Constants.PROP_MAIL_RECIPIENTS);
	}

	/** @see EmailMessage#getSender() */
	@Override
	public final String getSender() {
		return (String) getProperty(Constants.PROP_MAIL_SENDER);
	}

	/** @see EmailMessage#getSubject() */
	@Override
	public final String getSubject() {
		return (String) getProperty(Constants.PROP_MAIL_SUBJECT);
	}

	/** @see EmailMessage#getUid() */
	@Override
	public final String getUid() {
		return (String) getProperty(Constants.PROP_MAIL_UID);
	}

	/** @see EmailMessage#setDateSent(Date) */
	@Override
	public final void setDateSent(Date dateSent) {
		Validate.notNull(dateSent, "date sent cannot be null");
		setProperty(Constants.PROP_MAIL_DATE, dateSent.getTime());
	}

	/** @see EmailMessage#setRecipients(String[]) */
	@Override
	public final void setRecipients(String[] recipients) {
		Validate.notNull(recipients, "recipients cannot be null");
		Validate.noNullElements(recipients, "recipients cannot have null values");
		Validate.notEmpty(recipients, "recipients must have a value");
		setProperty(Constants.PROP_MAIL_RECIPIENTS, recipients);
	}

	/** @see EmailMessage#setSender(EmailAccount) */
	@Override
	public final void setSender(String sender) {
		Validate.notNull(sender, "sender cannot be null");
		Validate.notEmpty(sender, "sender must have a value");
		setProperty(Constants.PROP_MAIL_SENDER, sender);
	}

	/** @see EmailMessage#setSubject(String) */
	@Override
	public final void setSubject(String subject) {
		Validate.notNull(subject, "subject cannot be null");
		Validate.notEmpty(subject, "subject must have a value");
		setProperty(Constants.PROP_MAIL_SUBJECT, subject);
	}

	/** @see EmailMessage#setUid(String) */
	@Override
	public final void setUid(String uid) {
		Validate.notNull(uid, "uid cannot be null");
		Validate.notEmpty(uid, "uid must have a value");
		setProperty(Constants.PROP_MAIL_UID, uid);
	}
}