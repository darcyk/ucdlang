package ie.ucd.forlang.neo4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Traverser;
import org.neo4j.graphdb.Traverser.Order;

import com.fasterxml.jackson.databind.JsonNode;

@SuppressWarnings("deprecation")
public final class NodeImpl implements Node {

	private long id = Constants.DEF_OBJECT_ID;
	private final List<Label> labels = new ArrayList<Label>();
	private final Map<String, Object> properties = new HashMap<String, Object>();

	public NodeImpl(JsonNode root) {
		super();
		Validate.notNull(root);
		parse(root);
	}

	@Override
	public final void addLabel(Label label) {
		if (label != null && !hasLabel(label)) {
			labels.add(label);
		}
	}

	@Override
	public final Relationship createRelationshipTo(Node otherNode, RelationshipType type) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final void delete() {
		throw new UnsupportedOperationException();
	}

	@Override
	public final int getDegree() {
		throw new UnsupportedOperationException();
	}

	@Override
	public final int getDegree(Direction direction) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final int getDegree(RelationshipType type) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final int getDegree(RelationshipType type, Direction direction) {
		throw new UnsupportedOperationException();
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
	public final Iterable<Label> getLabels() {
		return labels;
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
	public final Iterable<Relationship> getRelationships() {
		throw new UnsupportedOperationException();
	}

	@Override
	public final Iterable<Relationship> getRelationships(Direction dir) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final Iterable<Relationship> getRelationships(Direction direction, RelationshipType... types) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final Iterable<Relationship> getRelationships(RelationshipType... types) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final Iterable<Relationship> getRelationships(RelationshipType type, Direction dir) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final Iterable<RelationshipType> getRelationshipTypes() {
		throw new UnsupportedOperationException();
	}

	@Override
	public final Relationship getSingleRelationship(RelationshipType type, Direction dir) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean hasLabel(Label label) {
		if (label == null) {
			return false;
		}
		return labels.contains(label);
	}

	@Override
	public final boolean hasProperty(String key) {
		if (key == null) {
			return false;
		}
		return properties.containsKey(key);
	}

	@Override
	public final boolean hasRelationship() {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean hasRelationship(Direction dir) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean hasRelationship(Direction direction, RelationshipType... types) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean hasRelationship(RelationshipType... types) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean hasRelationship(RelationshipType type, Direction dir) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final void removeLabel(Label label) {
		if (label != null) {
			labels.remove(label);
		}
	}

	@Override
	public final Object removeProperty(String key) {
		return properties.remove(key);
	}

	@Override
	public final void setProperty(String key, Object value) {
		properties.put(key, value);
	}

	@Override
	public final Traverser traverse(Order traversalOrder, StopEvaluator stopEvaluator,
			ReturnableEvaluator returnableEvaluator, Object... relationshipTypesAndDirections) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final Traverser traverse(Order traversalOrder, StopEvaluator stopEvaluator,
			ReturnableEvaluator returnableEvaluator, RelationshipType relationshipType, Direction direction) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final Traverser traverse(Order traversalOrder, StopEvaluator stopEvaluator,
			ReturnableEvaluator returnableEvaluator, RelationshipType firstRelationshipType, Direction firstDirection,
			RelationshipType secondRelationshipType, Direction secondDirection) {
		throw new UnsupportedOperationException();
	}

	private void parse(JsonNode root) {
		Iterator<JsonNode> nodes = null;
		Iterator<Map.Entry<String, JsonNode>> fields = null;
		Map.Entry<String, JsonNode> field = null;
		try {
			// parse metadata first: "metadata" : { "id" : 377, "labels" : [ "TwitterAccount" ] }
			id = root.get("metadata").get("id").asLong();
			nodes = root.get("metadata").get("labels").elements();
			while (nodes.hasNext()) {
				addLabel(DynamicLabel.label(nodes.next().asText()));
			}
			nodes = null;
			// parse data second: "data" : { "friendsCount" : 851, "location" : "Sandton, JHB", "description" : "Twitter's Man of Mystery.", }
			fields = root.get("data").fields();
			while (fields.hasNext()) {
				field = fields.next();
				setProperty(field.getKey(), field.getValue());
				field = null;
			}
		}
		catch (Exception e) {
			throw new RuntimeException("could not parse json to node", e);
		}
		finally {
			nodes = null;
			fields = null;
			field = null;
		}
	}
}