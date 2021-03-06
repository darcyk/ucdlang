package ie.ucd.forlang.neo4j.object;

import ie.ucd.forlang.neo4j.Constants;

import org.apache.commons.lang3.Validate;
import org.neo4j.graphdb.Node;

public final class EmailAccountImpl extends GraphObjectImpl implements EmailAccount {

	public EmailAccountImpl() {
		super();
	}

	public EmailAccountImpl(Node node) {
		super(node);
		setEmailAddress((String) node.getProperty(Constants.PROP_EMAIL_ADDRESS));
	}

	public EmailAccountImpl(String emailAddress) {
		super();
		setEmailAddress(emailAddress);
	}

	/** @see EmailAccount#getEmailAddress() */
	@Override
	public final String getEmailAddress() {
		return (String) getProperty(Constants.PROP_EMAIL_ADDRESS);
	}

	/** @see GraphObject#getGraphObjectType() */
	@Override
	public final GraphObjectType getGraphObjectType() {
		return GraphObjectType.EmailAccount;
	}

	/** @see GraphObject#getPrimaryPropertyName() */
	@Override
	public final String getPrimaryPropertyName() {
		return Constants.PROP_EMAIL_ADDRESS.toString();
	}

	/** @see GraphObject#getPrimaryPropertyValue() */
	@Override
	public final Object getPrimaryPropertyValue() {
		return getEmailAddress();
	}

	/** @see EmailAccount#setEmailAddress(String) */
	@Override
	public final void setEmailAddress(String emailAddress) {
		Validate.notNull(emailAddress, "email address cannot be null");
		Validate.notEmpty(emailAddress, "email address must have a value");
		setProperty(Constants.PROP_EMAIL_ADDRESS, emailAddress);
	}
}