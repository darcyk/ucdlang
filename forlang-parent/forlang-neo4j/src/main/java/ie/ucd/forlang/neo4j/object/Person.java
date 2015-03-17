package ie.ucd.forlang.neo4j.object;


/**
 * Object to represent a known person
 * 
 * @author Kev D'Arcy
 */
public interface Person extends GraphObject {
	
	public String getName();
	
	public void setName(String name);
}