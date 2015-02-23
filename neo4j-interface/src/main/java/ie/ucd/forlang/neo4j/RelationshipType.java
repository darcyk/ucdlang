package ie.ucd.forlang.neo4j;

public enum RelationshipType implements org.neo4j.graphdb.RelationshipType {
	KNOWNS, SENT, RECEIVED;
}