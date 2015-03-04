package ie.ucd.forlang.forlang.osi;

import java.util.List;

/**
 *
 * @author Kev D'Arcy
 */
public interface OsiSource {
    
    public List<String> getInfoFromDatabase();
    
    public void queryDataSource();
    
    // fix this
    public void addResultsToDatabase();
}
