package ie.ucd.forlang.neo4j.object;

public enum RelationshipType implements org.neo4j.graphdb.RelationshipType {
	KNOWNS, OWNS, PROBABLY_OWNS, FOLLOWS, SENT, RECEIVED;
}