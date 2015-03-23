package ie.ucd.forlang.neo4j.object;

import java.util.Iterator;
import java.util.Map.Entry;

/**
 * Generic object interface for Neo4j objects
 * 
 * @author Kev D'Arcy
 */
public interface GraphObject {

	public GraphObjectType getGraphObjectType();

	public long getId();
	
	public Iterator<Entry<String, Object>> getPropertiesIterator();
	
	public Object getProperty(String key);
	
	public void setId(long id);

	public void setProperty(String key, Object value);
}