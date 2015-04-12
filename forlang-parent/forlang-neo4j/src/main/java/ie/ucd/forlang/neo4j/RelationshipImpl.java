package ie.ucd.forlang.neo4j;

import org.apache.commons.lang3.Validate;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

public final class RelationshipImpl implements Relationship {

	private long id = Constants.DEF_OBJECT_ID;
	private Node start = null, end = null;
	private RelationshipType type = null;

	public RelationshipImpl(long id, Node start, Node end, RelationshipType type) {
		super();
		Validate.notNull(start, "start node cannot be null");
		Validate.notNull(end, "end node cannot be null");
		Validate.notNull(type, "type cannot be null");
		this.id = id;
		this.start = start;
		this.end = end;
		this.type = type;
	}

	@Override
	public final void delete() {
		throw new UnsupportedOperationException();
	}

	@Override
	public final Node getEndNode() {
		return end;
	}

	@Override
	public final GraphDatabaseService getGraphDatabase() {
		throw new UnsupportedOperationException();
	}

	@Override
	public final long getId() {
		return id;
	}

	@Override
	public final Node[] getNodes() {
		return new Node[] { start, end };
	}

	@Override
	public final Node getOtherNode(Node node) {
		Validate.notNull(node, "node cannot be null");
		Validate.notNull(node.getId(), "node id cannot be null");
		return (start.getId() == node.getId()) ? end : start;
	}

	@Override
	public final Object getProperty(String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final Object getProperty(String key, Object defaultValue) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final Iterable<String> getPropertyKeys() {
		throw new UnsupportedOperationException();
	}

	@Override
	public final Node getStartNode() {
		return start;
	}

	@Override
	public final RelationshipType getType() {
		return type;
	}

	@Override
	public final boolean hasProperty(String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean isType(RelationshipType type) {
		Validate.notNull(type, "type cannot be null");
		Validate.notNull(type.name(), "type name cannot be null");
		return (type.name().equals(this.type.name()));
	}

	@Override
	public final Object removeProperty(String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final void setProperty(String key, Object value) {
		throw new UnsupportedOperationException();
	}
}