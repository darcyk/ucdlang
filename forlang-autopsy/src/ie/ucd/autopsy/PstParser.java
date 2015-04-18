package ie.ucd.autopsy;

import static ie.ucd.autopsy.LegacyEmailParser.getRelModuleOutputPath;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sleuthkit.autopsy.ingest.IngestServices;
import org.sleuthkit.datamodel.AbstractFile;
import org.sleuthkit.datamodel.TskCoreException;

import com.pff.PSTAttachment;
import com.pff.PSTException;
import com.pff.PSTFile;
import com.pff.PSTFolder;
import com.pff.PSTMessage;

/**
 * Parser for extracting emails from pst/ost Mircosoft Outlook data files.
 *
 * @author jwallace
 */
public class PstParser {

	public enum ParseResult {
		ENCRYPT, ERROR, OK;
	}

	private static final Logger logger = Logger.getLogger(PstParser.class.getName());
	/**
	 * First four bytes of a pst file.
	 */
	private static int PST_HEADER = 0x2142444E;
	private StringBuilder errors;
	/**
	 * A map of PSTMessages to their Local path within the file's internal directory structure.
	 */
	private List<EmailMessage> results;
	private IngestServices services;

	public PstParser(IngestServices services) {
		results = new ArrayList<>();
		this.services = services;
		errors = new StringBuilder();
	}

	/**
	 * Identify a file as a pst/ost file by it's header.
	 *
	 * @param file
	 * @return
	 */
	public static boolean isPstFile(AbstractFile file) {
		byte[] buffer = new byte[4];
		try {
			int read = file.read(buffer, 0, 4);
			if (read != 4) {
				return false;
			}
			ByteBuffer bb = ByteBuffer.wrap(buffer);
			return bb.getInt() == PST_HEADER;
		}
		catch (TskCoreException ex) {
			logger.log(Level.WARNING, "Exception while detecting if a file is a pst file."); // NON-NLS
			return false;
		}
	}

	public final String getErrors() {
		return errors.toString();
	}

	/**
	 * Get the results of the parsing.
	 *
	 * @return
	 */
	public final List<EmailMessage> getResults() {
		return results;
	}

	/**
	 * Parse and extract email messages from the pst/ost file.
	 *
	 * @param file A pst or ost file.
	 * @return ParseResult: OK on success, ERROR on an error, ENCRYPT if failed because the file is encrypted.
	 */
	public final ParseResult parse(File file) {
		PSTFile pstFile;
		long failures;
		try {
			pstFile = new PSTFile(file);
			failures = processFolder(pstFile.getRootFolder(), "\\", true);
			if (failures > 0) {
				addErrorMessage("Failed to extract " + failures + " email messages");
			}
			return ParseResult.OK;
		}
		catch (PSTException | IOException ex) {
			String msg = file.getName() + ": Failed to create internal java-libpst PST file to parse:\n"
					+ ex.getMessage(); // NON-NLS
			logger.log(Level.WARNING, msg);
			return ParseResult.ERROR;
		}
		catch (IllegalArgumentException ex) {
			logger.log(Level.INFO, "Found encrypted PST file."); // NON-NLS
			return ParseResult.ENCRYPT;
		}
	}

	private void addErrorMessage(String msg) {
		errors.append("<li>").append(msg).append("</li>"); // NON-NLS
	}

	/**
	 * Add the attachments within the PSTMessage to the EmailMessage.
	 *
	 * @param email
	 * @param msg
	 */
	private void extractAttachments(EmailMessage email, PSTMessage msg) {
		int numberOfAttachments = msg.getNumberOfAttachments();
		String outputDirPath = LegacyEmailParser.getModuleOutputPath() + File.separator;
		for (int x = 0; x < numberOfAttachments; x++) {
			String filename = "";
			try {
				PSTAttachment attach = msg.getAttachment(x);
				long size = attach.getAttachSize();
				if (size >= services.getFreeDiskSpace()) {
					continue;
				}
				// both long and short filenames can be used for attachments
				filename = attach.getLongFilename();
				if (filename.isEmpty()) {
					filename = attach.getFilename();
				}
				String uniqueFilename = msg.getDescriptorNodeId() + "-" + filename;
				String outPath = outputDirPath + uniqueFilename;
				saveAttachmentToDisk(attach, outPath);
				EmailAttachment attachment = new EmailAttachment();
				long crTime = attach.getCreationTime().getTime() / 1000;
				long mTime = attach.getModificationTime().getTime() / 1000;
				String relPath = getRelModuleOutputPath() + File.separator + uniqueFilename;
				attachment.setName(filename);
				attachment.setCrTime(crTime);
				attachment.setmTime(mTime);
				attachment.setLocalPath(relPath);
				attachment.setSize(attach.getFilesize());
				email.addAttachment(attachment);
			}
			catch (PSTException | IOException ex) {
				addErrorMessage("Failed to extract PST attachment to disk\\: " + filename);
				logger.log(Level.WARNING, "Failed to extract attachment from pst file.", ex); // NON-NLS
			}
		}
	}

	/**
	 * Create an EmailMessage from a PSTMessage.
	 *
	 * @param msg
	 * @param localPath
	 * @return
	 */
	private EmailMessage extractEmailMessage(PSTMessage msg, String localPath) {
		EmailMessage email = new EmailMessage();
		//TODO
//		email.setRecipients(msg.getDisplayTo());
//		email.setCc(msg.getDisplayCC());
//		email.setBcc(msg.getDisplayBCC());
//		email.setSender(getSender(msg.getSenderName(), msg.getSenderEmailAddress()));
//		
//		email.setFrom(new EmailAddress(msg.getSenderEmailAddress(), msg.getSenderName()));
//		getAddresses(email.getTos(), msg.getTo());
//		getAddresses(email.getBccs(), msg.getBcc());
//		getAddresses(email.getCcs(), msg.getCc());
		
		email.setSentDate(msg.getMessageDeliveryTime());
		email.setTextBody(msg.getBody());
		email.setHtmlBody(msg.getBodyHTML());
		String rtf = "";
		try {
			rtf = msg.getRTFBody();
		}
		catch (PSTException | IOException ex) {
			logger.log(Level.INFO, "Failed to get RTF content from pst email."); // NON-NLS
		}
		email.setRtfBody(rtf);
		email.setLocalPath(localPath);
		email.setSubject(msg.getSubject());
		email.setId(msg.getDescriptorNodeId());
		if (msg.hasAttachments()) {
			extractAttachments(email, msg);
		}
		return email;
	}

	/**
	 * Process this folder and all subfolders, adding every email found to results. Accumulates the folder hierarchy
	 * path as it navigates the folder structure.
	 *
	 * @param folder The folder to navigate and process
	 * @param path The path to the folder within the pst/ost file's directory structure
	 * @throws PSTException
	 * @throws IOException
	 */
	private long processFolder(PSTFolder folder, String path, boolean root) {
		String newPath = (root ? path : path + "\\" + folder.getDisplayName());
		long failCount = 0L; // Number of emails that failed
		if (folder.hasSubfolders()) {
			List<PSTFolder> subFolders;
			try {
				subFolders = folder.getSubFolders();
			}
			catch (PSTException | IOException ex) {
				subFolders = new ArrayList<>();
				logger.log(Level.INFO, "java-libpst exception while getting subfolders: {0}", ex.getMessage()); // NON-NLS
			}
			for (PSTFolder f : subFolders) {
				failCount += processFolder(f, newPath, false);
			}
		}
		if (folder.getContentCount() != 0) {
			PSTMessage email;
			// A folder's children are always emails, never other folders.
			try {
				while ((email = (PSTMessage) folder.getNextChild()) != null) {
					results.add(extractEmailMessage(email, newPath));
				}
			}
			catch (PSTException | IOException ex) {
				failCount++;
				logger.log(Level.INFO, "java-libpst exception while getting emails from a folder: {0}", ex.getMessage()); // NON-NLS
			}
		}
		return failCount;
	}

	/**
	 * Extracts a PSTAttachment to the module output directory.
	 *
	 * @param attach
	 * @param outPath
	 * @return
	 * @throws IOException
	 * @throws PSTException
	 */
	private void saveAttachmentToDisk(PSTAttachment attach, String outPath) throws IOException, PSTException {
		try (InputStream attachmentStream = attach.getFileInputStream();
				FileOutputStream out = new FileOutputStream(outPath)) {
			// 8176 is the block size used internally and should give the best performance
			int bufferSize = 8176;
			byte[] buffer = new byte[bufferSize];
			int count = attachmentStream.read(buffer);
			while (count == bufferSize) {
				out.write(buffer);
				count = attachmentStream.read(buffer);
			}
			byte[] endBuffer = new byte[count];
			System.arraycopy(buffer, 0, endBuffer, 0, count);
			out.write(endBuffer);
		}
	}
}
