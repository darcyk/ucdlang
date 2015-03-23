package ie.ucd.forlang.neo4j.object;

import java.util.Iterator;
import java.util.Map.Entry;

/**
 * Generic object interface for Neo4j objects
 * 
 * @author Kev D'Arcy
 */
public interface GraphObject {

	/**
	 * The type of the object
	 * 
	 * @return GraphObjectType
	 */
	public GraphObjectType getGraphObjectType();

	/**
	 * The Neo4j generated node ID for this object
	 * 
	 * @return long
	 */
	public long getId();

	/**
	 * The name of the primary property for this object. Used for searching
	 * 
	 * @return String The property name
	 */
	public String getPrimaryPropertyName();

	/**
	 * The value of the primary property for this object. This must never be <code>null</code> and should uniquely
	 * identify the object. Used for searching
	 * 
	 * @return Object The property value
	 */
	public Object getPrimaryPropertyValue();

	/**
	 * Get an iterator for the properties on this objects. Used for adding new objects
	 * 
	 * @return String The property name
	 */
	public Iterator<Entry<String, Object>> getPropertiesIterator();

	public Object getProperty(String key);

	public void setId(long id);

	public void setProperty(String key, Object value);
}