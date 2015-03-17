package ie.ucd.forlang.neo4j.object;

public interface EmailAccount extends GraphObject {
	
	public String getEmailAddress();
	
	public void setEmailAddress(String emailAddress);
}