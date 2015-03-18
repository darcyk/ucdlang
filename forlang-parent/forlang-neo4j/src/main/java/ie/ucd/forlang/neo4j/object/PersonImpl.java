package ie.ucd.forlang.neo4j.object;

import org.apache.commons.lang.Validate;
import org.neo4j.graphdb.Node;

import ie.ucd.forlang.neo4j.Constants;

public class PersonImpl extends GraphObjectImpl implements Person {

	public PersonImpl() {
		super();
	}

	public PersonImpl(String name) {
		super();
		setName(name);
	}

	public PersonImpl(Node node) {
		super(node);
		setName((String) node.getProperty(Constants.PROP_NAME));
	}

	/** @see GraphObject#getGraphObjectType() */
	@Override
	public final GraphObjectType getGraphObjectType() {
		return GraphObjectType.Person;
	}

	/** @see Person#getName() */
	@Override
	public final String getName() {
		return (String) getProperty(Constants.PROP_NAME);
	}

	/** @see Person#setName(String) */
	@Override
	public final void setName(String name) {
		Validate.notNull(name, "name cannot be null");
		Validate.notEmpty(name, "name must have a value");
		setProperty(Constants.PROP_NAME, name);
	}
}