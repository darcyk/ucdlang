package ie.ucd.autopsy;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EmailMessage {

	private static final String EMPTY_STRING = "";
	private static final String HEX_STRING = "%02x";
	private static final String HASH_ALGO = "SHA-256";
	private final List<EmailAttachment> attachments = new ArrayList<EmailAttachment>();
	private final List<EmailAddress> bccs = new ArrayList<EmailAddress>();
	private final List<EmailAddress> ccs = new ArrayList<EmailAddress>();
	private EmailAddress from = null;
	private boolean hasAttachment = false;
	private String htmlBody = null;
	private long id = 0;
	private String localPath = null;
	private String rtfBody = null;
	private long sentDate = 0;
	private String subject = null;
	private String textBody = null;
	private final List<EmailAddress> tos = new ArrayList<EmailAddress>();

	public final void addAttachment(EmailAttachment a) {
		attachments.add(a);
		hasAttachment = true;
	}

	public final void addBcc(EmailAddress bccAddress) {
		bccs.add(bccAddress);
	}

	public final void addCc(EmailAddress ccAddress) {
		ccs.add(ccAddress);
	}

	public final void addTo(EmailAddress recipientAddress) {
		tos.add(recipientAddress);
	}

	public final List<EmailAttachment> getAttachments() {
		return attachments;
	}

	public final List<EmailAddress> getBccs() {
		return bccs;
	}

	public final String getBody() {
		return (htmlBody != null) ? htmlBody : (rtfBody != null) ? rtfBody : (textBody != null) ? textBody : EMPTY_STRING;
	}

	public final List<EmailAddress> getCcs() {
		return ccs;
	}

	public final EmailAddress getFrom() {
		return from;
	}

	public final String getHashCode() {
		MessageDigest md = null;
		StringBuilder sb = null;
		byte[] digest = null;
		try {
			md = MessageDigest.getInstance(HASH_ALGO);
			sb = new StringBuilder(1024);
			if (getSentDate() != 0) {
				sb.append(getSentDate());
			}
			if (getSubject() != null) {
				sb.append(getSubject());
			}
			if (getBody() != null) {
				sb.append(getBody());
			}
			digest = md.digest(sb.toString().getBytes());
			sb = null;
			sb = new StringBuilder();
			for (byte b : digest) {
				sb.append(String.format(HEX_STRING, b & 0xff));
			}
			return sb.toString();
		}
		catch (Exception e) {
			return null;
		}
		finally {
			md = null;
			sb = null;
			digest = null;
		}
	}

	public final String getHtmlBody() {
		return (htmlBody == null) ? EMPTY_STRING : htmlBody;
	}

	public final long getId() {
		return id;
	}

	public final String getLocalPath() {
		return (localPath == null) ? EMPTY_STRING : localPath;
	}

	public final String getRtfBody() {
		return (rtfBody == null) ? EMPTY_STRING : rtfBody;
	}

	public final long getSentDate() {
		return sentDate;
	}

	public final String getSubject() {
		return (subject == null) ? EMPTY_STRING : subject;
	}

	public final String getTextBody() {
		return (textBody == null) ? EMPTY_STRING : textBody;
	}

	public final List<EmailAddress> getTos() {
		return tos;
	}

	public final boolean hasAttachment() {
		return hasAttachment;
	}

	public final void setFrom(EmailAddress from) {
		this.from = from;
	}

	public final void setHtmlBody(String htmlBody) {
		this.htmlBody = htmlBody;
	}

	public final void setId(long id) {
		this.id = id;
	}

	public final void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	public final void setRtfBody(String rtfBody) {
		this.rtfBody = rtfBody;
	}

	public final void setSentDate(Date sentDate) {
		if (sentDate != null) {
			this.sentDate = sentDate.getTime();
		}
	}

	public final void setSentDate(long sentDate) {
		this.sentDate = sentDate;
	}

	public final void setSubject(String subject) {
		this.subject = subject;
	}

	public final void setTextBody(String textBody) {
		this.textBody = textBody;
	}
}