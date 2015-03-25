package ie.ucd.forlang.neo4j;

import java.io.File;

import org.apache.commons.lang.Validate;

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
	 * @param dir File The file root to check
	 * @throws IllegalArgumentException If any of the criteria are not met
	 */
	public static final void validDatabaseRoot(File dir) throws IllegalArgumentException {
		Validate.notNull(dir, "dir cannot be null");
		Validate.isTrue(dir.exists(), "dir does not exist");
		Validate.isTrue(dir.isDirectory(), "dir is not a directoy");
		Validate.isTrue(dir.canWrite(), "dir is not writable");
	}
}