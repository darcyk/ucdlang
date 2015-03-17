package ie.ucd.forlang.neo4j.object;

import ie.ucd.forlang.neo4j.Constants;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.neo4j.graphdb.Node;

public abstract class GraphObjectImpl implements GraphObject {

	private long id = Constants.DEF_OBJECT_ID;
	private Node node = null;
	private final Map<String, Object> properties = new HashMap<String, Object>();

	public GraphObjectImpl() {
		super();
	}

	public GraphObjectImpl(long id) {
		super();
		setId(id);
	}

	public GraphObjectImpl(Node node) {
		super();
		this.node = node;
		setId(node.getId());
	}

	/** @see GraphObject#getId() */
	@Override
	public final long getId() {
		return id;
	}
	
	/** @see GraphObject#getPropertiesIterator() */
	@Override
	public final Iterator<Entry<String, Object>> getPropertiesIteratory() {
		return properties.entrySet().iterator();
	}

	/** @see GraphObject#getProperty(String) */
	@Override
	public final Object getProperty(String key) {
		return properties.get(key);
	}

	/** @see GraphObject#setId(long) */
	@Override
	public final void setId(long id) {
		this.id = id;
	}

	/** @see GraphObject#setProperty(String, Object) */
	@Override
	public final void setProperty(String key, Object value) {
		properties.put(key, value);
	}
}