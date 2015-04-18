package ie.ucd.autopsy;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.CharConversionException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.james.mime4j.dom.BinaryBody;
import org.apache.james.mime4j.dom.Body;
import org.apache.james.mime4j.dom.Entity;
import org.apache.james.mime4j.dom.Message;
import org.apache.james.mime4j.dom.Multipart;
import org.apache.james.mime4j.dom.TextBody;
import org.apache.james.mime4j.dom.address.AddressList;
import org.apache.james.mime4j.dom.address.Mailbox;
import org.apache.james.mime4j.dom.field.ContentDispositionField;
import org.apache.james.mime4j.dom.field.ContentTypeField;
import org.apache.james.mime4j.mboxiterator.CharBufferWrapper;
import org.apache.james.mime4j.mboxiterator.MboxIterator;
import org.apache.james.mime4j.message.DefaultMessageBuilder;
import org.apache.james.mime4j.stream.MimeConfig;
import org.apache.tika.parser.txt.CharsetDetector;
import org.apache.tika.parser.txt.CharsetMatch;

/**
 * A parser that extracts information about email messages and attachments from a mbox file.
 *
 * @author jwallace
 */
public class MboxParser {

	/**
	 * The mime type string for html text.
	 */
	private static final String HTML_TYPE = "text/html"; // NON-NLS
	private static final Logger logger = Logger.getLogger(MboxParser.class.getName());
	private StringBuilder errors;
	/**
	 * The local path of the mbox file.
	 */
	private String localPath;
	private DefaultMessageBuilder messageBuilder;

	// private IngestServices services;
	public MboxParser(String localPath) {
		// this.services = services;
		this.localPath = localPath;
		messageBuilder = new DefaultMessageBuilder();
		MimeConfig config = MimeConfig.custom().setMaxLineLen(-1).build();
		// disable line length checks.
		messageBuilder.setMimeEntityConfig(config);
		errors = new StringBuilder();
	}

	public static final boolean isValidMimeTypeMbox(byte[] buffer) {
		return (new String(buffer)).startsWith("From "); // NON-NLS
	}

	public final String getErrors() {
		return errors.toString();
	}

	/**
	 * Parse the mbox file and get the email messages.
	 *
	 * @param mboxFile
	 * @return a list of the email messages in the mbox file.
	 */
	public final List<EmailMessage> parse(File mboxFile) {
		// Detect possible charsets
		List<CharsetEncoder> encoders = getPossibleEncoders(mboxFile);
		CharsetEncoder theEncoder = null;
		Iterable<CharBufferWrapper> mboxIterator = null;
		// Loop through the possible encoders and find the first one that works.
		// That will usually be one of the first ones.
		for (CharsetEncoder encoder : encoders) {
			try {
				mboxIterator = MboxIterator.fromFile(mboxFile).charset(encoder.charset()).build();
				theEncoder = encoder;
				break;
			}
			catch (CharConversionException | UnsupportedCharsetException ex) {
				// Not the right encoder
			}
			catch (IllegalArgumentException ex) {
				// Not the right encoder
			}
			catch (IOException ex) {
				logger.log(Level.WARNING, "couldn't find mbox file.", ex); // NON-NLS
				addErrorMessage("Failed to read mbox file from disk.");
				return new ArrayList<>();
			}
		}
		// If no encoders work, post an error message and return.
		if (mboxIterator == null || theEncoder == null) {
			addErrorMessage("Could not find appropriate charset encoder.");
			return new ArrayList<>();
		}
		List<EmailMessage> emails = new ArrayList<>();
		long failCount = 0;
		// Parse each message and extract an EmailMessage structure
		for (CharBufferWrapper message : mboxIterator) {
			try {
				Message msg = messageBuilder.parseMessage(message.asInputStream(theEncoder.charset()));
				emails.add(extractEmail(msg));
			}
			catch (IOException ex) {
				logger.log(Level.WARNING, "Failed to get message from mbox: {0}", ex.getMessage()); // NON-NLS
				failCount++;
			}
		}
		if (failCount > 0) {
			addErrorMessage("Failed to extract " + failCount + " email messages.");
		}
		return emails;
	}

	private void addErrorMessage(String msg) {
		errors.append("<li>").append(msg).append("</li>"); // NON-NLS
	}

	/**
	 * Use the information stored in the given mime4j message to populate an EmailMessage.
	 *
	 * @param msg
	 * @return
	 */
	private EmailMessage extractEmail(Message msg) {
		EmailMessage email = new EmailMessage();
		// Basic Info
		if (msg.getFrom() != null && msg.getFrom().size() > 0) {
			Mailbox mb = msg.getFrom().get(0);
			email.setFrom(new EmailAddress(mb.getAddress(), mb.getName()));
		}
		getAddresses(email.getTos(), msg.getTo());
		getAddresses(email.getBccs(), msg.getBcc());
		getAddresses(email.getCcs(), msg.getCc());
		email.setSubject(msg.getSubject());
		email.setSentDate(msg.getDate());
		email.setLocalPath(localPath);
		// Body
		if (msg.isMultipart()) {
			handleMultipart(email, (Multipart) msg.getBody());
		}
		else {
			handleTextBody(email, (TextBody) msg.getBody(), msg.getMimeType());
		}
		return email;
	}

	/**
	 * Get a String representation of the AddressList (which is a list of email addresses).
	 *
	 * @param addressList
	 * @return
	 */
	private void getAddresses(List<EmailAddress> list, AddressList addressList) {
		if (addressList != null) {
			for (Mailbox m : addressList.flatten()) {
				list.add(new EmailAddress(m.getAddress(), m.getName()));
			}
		}
	}

	/**
	 * Get a list of the possible encoders for the given mboxFile using Tika's CharsetDetector. At a minimum, returns
	 * the standard built in charsets.
	 *
	 * @param mboxFile
	 * @return
	 */
	private List<CharsetEncoder> getPossibleEncoders(File mboxFile) {
		InputStream is;
		List<CharsetEncoder> possibleEncoders = new ArrayList<>();
		possibleEncoders.add(StandardCharsets.ISO_8859_1.newEncoder());
		possibleEncoders.add(StandardCharsets.US_ASCII.newEncoder());
		possibleEncoders.add(StandardCharsets.UTF_16.newEncoder());
		possibleEncoders.add(StandardCharsets.UTF_16BE.newEncoder());
		possibleEncoders.add(StandardCharsets.UTF_16LE.newEncoder());
		possibleEncoders.add(StandardCharsets.UTF_8.newEncoder());
		try {
			is = new BufferedInputStream(new FileInputStream(mboxFile));
		}
		catch (FileNotFoundException ex) {
			logger.log(Level.WARNING, "Failed to find mbox file while detecting charset"); // NON-NLS
			return possibleEncoders;
		}
		try {
			CharsetDetector detector = new CharsetDetector();
			detector.setText(is);
			CharsetMatch[] matches = detector.detectAll();
			for (CharsetMatch match : matches) {
				try {
					possibleEncoders.add(Charset.forName(match.getName()).newEncoder());
				}
				catch (UnsupportedCharsetException | IllegalCharsetNameException ex) {
					// Don't add unsupported charsets to the list
				}
			}
			return possibleEncoders;
		}
		catch (IOException | IllegalArgumentException ex) {
			logger.log(Level.WARNING, "Failed to detect charset of mbox file.", ex); // NON-NLS
			return possibleEncoders;
		}
		finally {
			try {
				is.close();
			}
			catch (IOException ex) {
				logger.log(Level.INFO, "Failed to close input stream"); // NON-NLS
			}
		}
	}

	/**
	 * Extract the attachment out of the given entity. Should only be called if e.getDispositionType() == "attachment"
	 *
	 * @param email
	 * @param e
	 */
	private void handleAttachment(EmailMessage email, Entity e) {
		String outputDirPath = LegacyEmailParser.getModuleOutputPath() + File.separator;
		String filename = e.getFilename();
		// sanitize name. Had an attachment with a Japanese encoded path that
		// invalid characters and attachment could not be saved.
		filename = filename.replaceAll("\\?", "_");
		filename = filename.replaceAll("<", "_");
		filename = filename.replaceAll(">", "_");
		filename = filename.replaceAll(":", "_");
		filename = filename.replaceAll("\"", "_");
		filename = filename.replaceAll("/", "_");
		filename = filename.replaceAll("\\\\", "_");
		filename = filename.replaceAll("|", "_");
		filename = filename.replaceAll("\\*", "_");
		// also had some crazy long names, so make random one if we get those.
		// also from Japanese image that had encoded name
		if (filename.length() > 64) {
			filename = UUID.randomUUID().toString();
		}
		String uniqueFilename = filename + "-" + email.getSentDate();
		String outPath = outputDirPath + uniqueFilename;
		FileOutputStream fos;
		BinaryBody bb;
		try {
			fos = new FileOutputStream(outPath);
		}
		catch (FileNotFoundException ex) {
			addErrorMessage("Failed to extract MBOX attachment to disk\\: " + outPath);
			logger.log(Level.INFO, "Failed to create file output stream for: " + outPath, ex); // NON-NLS
			return;
		}
		try {
			Body b = e.getBody();
			if (b instanceof BinaryBody) {
				bb = (BinaryBody) b;
				bb.writeTo(fos);
			}
			else {
				// This could potentially be other types. Only seen this once.
			}
		}
		catch (IOException ex) {
			logger.log(Level.INFO, "Failed to write mbox email attachment to disk.", ex); // NON-NLS
			addErrorMessage("Failed to extract attachment to disk\\: " + filename);
			return;
		}
		finally {
			try {
				fos.close();
			}
			catch (IOException ex) {
				logger.log(Level.INFO, "Failed to close file output stream", ex); // NON-NLS
			}
		}
		EmailAttachment attach = new EmailAttachment();
		attach.setName(filename);
		attach.setLocalPath(LegacyEmailParser.getRelModuleOutputPath() + File.separator + uniqueFilename);
		attach.setSize(new File(outPath).length());
		email.addAttachment(attach);
	}

	/**
	 * Handle a multipart mime message. Recursively calls handleMultipart if one of the body parts is another multipart.
	 * Otherwise, calls the correct method to extract information out of each part of the body.
	 *
	 * @param email
	 * @param multi
	 */
	private void handleMultipart(EmailMessage email, Multipart multi) {
		for (Entity e : multi.getBodyParts()) {
			if (e.isMultipart()) {
				handleMultipart(email, (Multipart) e.getBody());
			}
			else if (e.getDispositionType() != null
					&& e.getDispositionType().equals(ContentDispositionField.DISPOSITION_TYPE_ATTACHMENT)) {
				handleAttachment(email, e);
			}
			else if (e.getMimeType().equals(HTML_TYPE) || e.getMimeType().equals(ContentTypeField.TYPE_TEXT_PLAIN)) {
				handleTextBody(email, (TextBody) e.getBody(), e.getMimeType());
			}
			else {
				// Ignore other types.
			}
		}
	}

	/**
	 * Extract text out of a body part of the message. Handles text and html mime types. Throws away all other types.
	 * (only other example I've seen is text/calendar)
	 *
	 * @param email
	 * @param tb
	 * @param type The Mime type of the body.
	 */
	private void handleTextBody(EmailMessage email, TextBody tb, String type) {
		BufferedReader r;
		try {
			r = new BufferedReader(tb.getReader());
			StringBuilder bodyString = new StringBuilder();
			String line;
			while ((line = r.readLine()) != null) {
				bodyString.append(line).append("\n");
			}
			switch (type) {
			case ContentTypeField.TYPE_TEXT_PLAIN:
				email.setTextBody(bodyString.toString());
				break;
			case HTML_TYPE:
				email.setHtmlBody(bodyString.toString());
				break;
			default:
				// Not interested in other text types.
				break;
			}
		}
		catch (IOException ex) {
			logger.log(Level.WARNING, "Error getting text body of mbox message", ex); // NON-NLS
		}
	}
}
