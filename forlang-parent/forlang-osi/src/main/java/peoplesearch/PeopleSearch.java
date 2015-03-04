/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peoplesearch;

/**
 *
 * @author dm
 */
public class PeopleSearch {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        CSVutil uti=new CSVutil();
        uti.readCSV(Constants.CSV_FILE_NAME);
        System.out.println("Program End");
        
    }
    
}
