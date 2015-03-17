package ie.ucd.forlang.neo4j.object;

import ie.ucd.forlang.neo4j.Constants;

import org.neo4j.graphdb.Node;

public final class EmailAccountImpl extends GraphObjectImpl implements EmailAccount {

	private String emailAddress = null;
	
	public EmailAccountImpl() {
		super();
	}

	public EmailAccountImpl(String emailAddress) {
		super();
		setEmailAddress(emailAddress);
	}
	
	public EmailAccountImpl(Node node) {
		super(node);
		setEmailAddress((String) node.getProperty(Constants.PROP_EMAIL_ADDRESS));
	}

	/** @see GraphObject#getGraphObjectType() */
	@Override
	public final GraphObjectType getGraphObjectType() {
		return GraphObjectType.EmailAccount;
	}

	/** @see EmailAccount#getEmailAddress() */
	@Override
	public final String getEmailAddress() {
		return emailAddress;
	}

	/** @see EmailAccount#setEmailAddress(String) */
	@Override
	public final void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
}