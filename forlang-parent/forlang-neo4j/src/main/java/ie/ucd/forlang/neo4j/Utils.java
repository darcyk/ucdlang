package ie.ucd.forlang.neo4j;

import java.io.File;
import java.net.URL;

import org.apache.commons.lang3.Validate;

/** General utility methods */
public final class Utils {

	/**
	 * Validate that the proposed database root folder meets the following criteria:
	 * <ul>
	 * <li>is not <code>null</code></li>
	 * <li>exists</li>
	 * <li>is a directory</li>
	 * <li>is writable</li>
	 * </ul>
	 * 
	 * @param dir String The file root to check
	 * @return File The passed root as a <code>File</code> object
	 * @throws IllegalArgumentException If any of the criteria are not met
	 */
	public static final File validDatabaseRoot(String dir) throws IllegalArgumentException {
		Validate.notEmpty(dir, "dir cannot be null");
		File fdir = null;
		try {
			fdir = new File(dir);
			Validate.isTrue(fdir.exists(), "dir does not exist");
			Validate.isTrue(fdir.isDirectory(), "dir is not a directoy");
			Validate.isTrue(fdir.canWrite(), "dir is not writable");
			return fdir;
		}
		catch (Exception e) {
			throw new IllegalArgumentException("invalid file root for graph database: " + dir, e);
		}
		finally {
			fdir = null;
		}
	}

	/**
	 * Validate that the proposed database URL meets the following criteria:
	 * <ul>
	 * <li>is not <code>null</code></li>
	 * <li>is not empty</li>
	 * <li>is valid URL syntactically</li>
	 * </ul>
	 * 
	 * @param dbUrl String The URL to check
	 * @throws IllegalArgumentException If any of the criteria are not met
	 */
	public static final void validDatabaseUrlRoot(String dbUrl) throws IllegalArgumentException {
		Validate.notEmpty(dbUrl, "dbUrl must have a value");
		try {
			new URL(dbUrl);
		}
		catch (Exception e) {
			throw new IllegalArgumentException("invalid url: " + dbUrl);
		}
	}
}