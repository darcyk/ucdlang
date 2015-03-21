package ie.ucd.forlang.neo4j.object;

/**
 * Object to represent a known person
 * 
 * @author Kev D'Arcy
 */
public interface Person extends GraphObject {

	/**
	 * Get the name of this <code>Person</code>
	 * 
	 * @return String The persons full name
	 */
	public String getName();

	/**
	 * Set the name of this <code>Person</code>
	 * 
	 * @param name String The persons name. Cannot be <code>null</code> or an empty string
	 */
	public void setName(String name);
}