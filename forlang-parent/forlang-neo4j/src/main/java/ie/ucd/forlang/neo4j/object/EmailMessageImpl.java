package ie.ucd.forlang.neo4j.object;

import java.util.Date;
import java.util.List;

public final class EmailMessageImpl extends GraphObjectImpl implements EmailMessage {

	private Date dateSent = null;
	private List<EmailAccount> recipients = null;
	private EmailAccount sender = null;
	private String subject = null;

	public EmailMessageImpl() {
		super();
	}

	public EmailMessageImpl(long id, Date dateSent, List<EmailAccount> recipients, EmailAccount sender, String subject) {
		super(id);
		setDateSent(dateSent);
		setRecipients(recipients);
		setSender(sender);
		setSubject(subject);
	}

	/** @see GraphObject#getGraphObjectType() */
	@Override
	public final GraphObjectType getGraphObjectType() {
		return GraphObjectType.EmailMessage;
	}

	/** @see EmailMessage#getDateSent() */
	@Override
	public final Date getDateSent() {
		return dateSent;
	}

	/** @see EmailMessage#getRecipients() */
	@Override
	public final List<EmailAccount> getRecipients() {
		return recipients;
	}

	/** @see EmailMessage#getSender() */
	@Override
	public final EmailAccount getSender() {
		return sender;
	}

	/** @see EmailMessage#getSubject() */
	@Override
	public final String getSubject() {
		return subject;
	}

	/** @see EmailMessage#setDateSent(Date) */
	@Override
	public final void setDateSent(Date dateSent) {
		this.dateSent = dateSent;
	}

	/** @see EmailMessage#setRecipients(List) */
	@Override
	public final void setRecipients(List<EmailAccount> recipients) {
		this.recipients = recipients;
	}

	/** @see EmailMessage#setSender(EmailAccount) */
	@Override
	public final void setSender(EmailAccount sender) {
		this.sender = sender;
	}

	/** @see EmailMessage#setSubject(String) */
	@Override
	public final void setSubject(String subject) {
		this.subject = subject;
	}
}