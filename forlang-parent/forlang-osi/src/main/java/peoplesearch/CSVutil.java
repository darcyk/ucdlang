/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peoplesearch;


import com.opencsv.CSVReader;
import java.io.File;
import java.io.FileReader;
import java.util.Iterator;
import com.SocialAccess.TwitterSearch;
import java.util.ArrayList;
import java.util.List;




/**
 *
 * @author dm
 */

public class CSVutil {
public static List<String[]> readCSV(String filePath) {
    List<String[]>people=new ArrayList<>();
    
        
        try{
         File file=new File(filePath);
         FileReader reader=new FileReader(file);
         
         CSVReader csvReader=new CSVReader(reader,',');
         Iterator<String[]> itr=csvReader.iterator();
         
         while(itr.hasNext()){
             String[] data=itr.next();
             String[] TwitterConnections=new String[2];
             System.out.println(data[0]+" "+data[1]);
             TwitterConnections=TwitterSearch.FriendshipStatus(data);
             
             System.out.println(TwitterConnections[0]);
             System.out.println(TwitterConnections[1]);
             people.add(data);
             
         } 
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        
    return people;
}


}