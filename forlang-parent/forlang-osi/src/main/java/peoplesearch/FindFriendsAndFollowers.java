/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peoplesearch;

import ie.ucd.forlang.neo4j.EmbeddedGraphManager;
import ie.ucd.forlang.neo4j.GraphManager;
import ie.ucd.forlang.neo4j.object.EmailAccountImpl;
import static ie.ucd.forlang.neo4j.object.GraphObjectType.TwitterAccount;
import ie.ucd.forlang.neo4j.object.Person;
import ie.ucd.forlang.neo4j.object.PersonImpl;
import ie.ucd.forlang.neo4j.object.TwitterAccount;
import ie.ucd.forlang.neo4j.object.TwitterAccountImpl;
import twitter4j.IDs;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.TwitterException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.*;
import java.util.Date;
import java.util.List;


/**
 *
 * @author telecareaps
 */
public class FindFriendsAndFollowers {
    
public void GetFollowersIDs(){
try {
    // I need to pass the Person name and the TwitterID.
            String targetname="Philip Bergkvist";
            Twitter twitter = new TwitterFactory().getInstance();
            long cursor = -1;
            String[] pep = new String [10];
            pep[0]="2730631792";
            IDs ids;
            
            ResponseList<User> users1 = null;
            ResponseList<User> users2 = null;
            System.out.println("Listing followers's ids.");
            
            GraphManager mgr = EmbeddedGraphManager.getInstance();

            mgr.init(new File("/Users/telecareaps/Downloads/neo4j-community-2.1.7/data/test.db"));           
            mgr.addTwitterAccount(new TwitterAccountImpl(new Date(), "I am Studying", 13, 82, true,"Aalborg", "Philiptwoshoes", 2730631792L));
 
            
            List<TwitterAccount> twitteraccountslist;
            twitteraccountslist = null;
            twitteraccountslist = mgr.listTwitterAccounts();    
            System.out.println("the number of twitter account in the neo4J DB is"+ twitteraccountslist.size());
            for (TwitterAccount Twit : twitteraccountslist) {
            
                do {
                    if (0 < twitteraccountslist.size()) {
                    //if (0 < pep.length) {
                        ids = twitter.getFollowersIDs(Twit.getScreenName(), cursor); //.getFollowersIDs(pep[0], cursor);
                        //ids = twitter.getFollowersIDs("Philiptwoshoes", cursor); //.getFollowersIDs(pep[0], cursor);
                        users1 = twitter.getFollowersList(Twit.getScreenName(), cursor);

                        users2 = twitter.getFriendsList(Twit.getScreenName(), cursor);

                    } else {
                        ids = twitter.getFollowersIDs(cursor);

                    }

                        for (User user : users1) {
                        System.out.println("the follower called "+ user.getName() + " with twitter handler " + user.getScreenName());
                        String username= user.getName();
                        //mgr.addPerson(new PersonImpl(username));
                        Date Creation= user.getCreatedAt();
                        String descript=user.getDescription();
                        boolean empty1= user.getDescription().isEmpty();
                        if (empty1==true){
                            descript=" ";
                        }
                        int followers=user.getFollowersCount();
                        int following=user.getFriendsCount();
                        boolean geo=user.isGeoEnabled();
                        String loc=user.getLocation();
                        boolean empty2= user.getLocation().isEmpty();
                        if (empty2==true){
                            loc=" ";
                        }
                        String screenname=user.getScreenName();
                        boolean empty3= user.getScreenName().isEmpty();
                        if (empty3==true){
                            screenname=" ";
                        }
                        long twitID=user.getId();
                        //mgr.addTwitterAccount(new TwitterAccountImpl(new Date(), "", 13, 82, true,"", "Philiptwoshoes", 2730631798L));
                        //mgr.addTwitterAccount(new TwitterAccountImpl(Creation,descript,followers,following,geo,loc,screenname,twitID));
                        //mgr.addTwitterAccount(new TwitterAccountImpl(user.getCreatedAt(),user.getDescription(),user.getFollowersCount(),user.getFriendsCount(),user.isGeoEnabled(),user.getLocation(),user.getScreenName(),22));
                        mgr.linkPersonToTwitterAccount(new PersonImpl(username), new TwitterAccountImpl(Creation,descript,followers,following,geo,loc,screenname,twitID));
                        mgr.linkTwitterAccounts(new TwitterAccountImpl(Creation,descript,followers,following,geo,loc,screenname,twitID), Twit);
                        }

                        System.out.println("The total number of followers is: "+ users1.size());
                        // the same procedure for the Following
                        for (User user : users2) {
                        System.out.println("the follower called "+ user.getName() + " with twitter handler " + user.getScreenName());
                        String username1= user.getName();
                        //mgr.addPerson(new PersonImpl(username1));
                        Date Creation= user.getCreatedAt();
                        String descript=user.getDescription();
                        boolean empty1= user.getDescription().isEmpty();
                        if (empty1==true){
                            descript=" ";
                        }
                        int followers=user.getFollowersCount();
                        int following=user.getFriendsCount();
                        boolean geo=user.isGeoEnabled();
                        String loc=user.getLocation();
                        boolean empty2= user.getLocation().isEmpty();
                        if (empty2==true){
                            loc=" ";
                        }
                        String screenname=user.getScreenName();
                        boolean empty3= user.getScreenName().isEmpty();
                        if (empty3==true){
                            screenname=" ";
                        }
                        long twitID=user.getId();
                        //mgr.addTwitterAccount(new TwitterAccountImpl(Creation,descript,followers,following,geo,loc,screenname,twitID));
                        mgr.linkPersonToTwitterAccount(new PersonImpl(username1), new TwitterAccountImpl(Creation,descript,followers,following,geo,loc,screenname,twitID));
                        mgr.linkTwitterAccounts(Twit,new TwitterAccountImpl(Creation,descript,followers,following,geo,loc,screenname,twitID));
                        }
                        System.out.println("The total number of friend is: "+ users2.size());
                         
                     //}
                } while ((cursor = ids.getNextCursor()) != 0);
                mgr.destroy(); // I have to check that the second iteration works fine, because i could not test that.
            } 
            System.exit(0);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to get followers' ids: " + te.getMessage());
            System.exit(-1);
        }
}
     
    
    public static void main(String[] args) {
        FindFriendsAndFollowers su =new FindFriendsAndFollowers();
        su.GetFollowersIDs();
    }
    
}



//    public void searchOnTwitter(){
//        int page=1;
//        try{
//        Twitter twitter = new TwitterFactory().getInstance();
//       ResponseList<User> users= twitter.searchUsers("Philip Bergkvist", page);
//       //test by JPS to see what can we obtain
//       //ResponseList<User> users= twitter. ("Joan Manuel Palos", page);
//       do {
//                //users = twitter.searchUsers(args[0], page);
//                for (User user : users) {
//                    if (user.getStatus() != null) {
//                        System.out.println("@" + user.getScreenName() + " ;" + user.getFollowersCount());
//                    } else {
//                        // the user is protected
//                        System.out.println("@" + user.getScreenName());
//                    }
//                }
//                page++;
//                System.out.println(page);
//            } while (users.size() != 0 && page < 1);
//            System.out.println("I'v found " + users.size());
//       
//       
//        }catch(Exception ex){
//            System.out.println(ex.getMessage());
//        }
//        
//    }
//    
//    public void searchOnTwitter1(){
//        int page=1;
//        String username= "Philip Bergkvist";
//        try{
//        Twitter twitter = new TwitterFactory().getInstance();
//       ResponseList<User> users= twitter.searchUsers(username, page);
//       //test by JPS to see what can we obtain
//       //ResponseList<User> users= twitter. ("Joan Manuel Palos", page);
//       do {
//                //users = twitter.searchUsers(args[0], page);
//                for (User user : users) {
//                    if (user.getStatus() != null) {
//                        System.out.println("The user " + username 
//                                + " has twitter handle @" + user.getScreenName() 
//                                + " ;" + user.getId()
//                                + " ;" + user.getFollowersCount()
//                                + " ;" + user.getFriendsCount()
//                                + " ;" + user.getDescription()
//                                + " ;" + user.getDescriptionURLEntities()
//                                + " ;" + user.getCreatedAt()
//                                + " ;" + user.getLocation()
//                                + " ;" + user.getURL()
//                                + " ; is GEO enabled:" + user.isGeoEnabled()
//                                + "\n" //add end of line in the file
//                        );
//                        
//                        // I will save the query back to a File which it will be .CSV and then it can be easily imported in Neo4J
//                        try {
//      //create a buffered reader that connects to the console, we use it so we can read lines
//      BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
//
//      //read a line from the console
//      String lineFromInput = "The user " + username 
//                                + " has twitter handle @" + user.getScreenName() 
//                                + " ;" + user.getId()
//                                + " ;" + user.getFollowersCount()
//                                + " ;" + user.getFriendsCount()
//                                + " ;" + user.getDescription()
//                                + " ;" + user.getDescriptionURLEntities()
//                                + " ;" + user.getCreatedAt()
//                                + " ;" + user.getDescription()
//                                + " ;" + user.getLocation()
//                                
//                                + " ;" + user.getURL()
//                                + " ; is GEO enabled:" + user.isGeoEnabled();
//              
//              //in.readLine();
//
//      //create an print writer for writing to a file
//      PrintWriter out = new PrintWriter(new FileWriter("queryoutput.csv"));
//
//      //output to the file a line
//      out.println(lineFromInput);
//
//      //close the file (VERY IMPORTANT!)
//      out.close();
//   }
//      catch(IOException e1) {
//        System.out.println("Error during reading/writing");
//   }
//                      //  System.out.println
//                    } else {
//                        // the user is protected
//                        System.out.println("@" + user.getScreenName());
//                    }
//                }
//                page++;
//                System.out.println(page);
//            } while (users.size() != 0 && page < 1);
//            System.out.println("I'v found " + users.size());
//       
//       
//        }catch(Exception ex){
//            System.out.println(ex.getMessage());
//        }
//        
//    }
//    
//    
//    public void searchOnTwitter2(){
//        int page=1;
//        int accountsfound=0;
//        String username= "Philip Bergkvist";
//        try{
//        Twitter twitter = new TwitterFactory().getInstance();
//       ResponseList<User> users;
//       //test by JPS to see what can we obtain
//       //ResponseList<User> users= twitter. ("Joan Manuel Palos", page);
//       //create a buffered reader that connects to the console, we use it so we can read lines
//        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
//        //create an print writer for writing to a file
//        PrintWriter out = new PrintWriter(new FileWriter("queryoutput.csv"));
//       TwitterAccount twit1 = null;
//     
//       do {
//           users= twitter.searchUsers(username, page);
//            
//                        
//            
//                //users = twitter.searchUsers(args[0], page);
//            String lineFromInput = "Nickname"+","+"TwitterID"+","+"Followers"+","+"Following"+","+"Status"+","+"CreatedAt"+","+"Location"+","+"GeolocationEnabled";
//            //output to the file a line
//            out.println(lineFromInput);    
//            for (User user : users) {
//                    if (user.getStatus() != null) {
//                        System.out.println(
//                                user.getScreenName() 
//                                + "\t" + user.getId()
//                                + "\t" + user.getFollowersCount()
//                                + "\t" + user.getFriendsCount()
//                                + "\t" + user.getDescription()
//                                + "\t" + user.getCreatedAt()
//                                + "\t" + user.getLocation()
//                                + "\t" + user.isGeoEnabled()
//                                + "\n" //add end of line in the file
//                        );
//                        accountsfound++;
//                        
//                        // I will save the query back to a File which it will be .CSV and then it can be easily imported in Neo4J
//                        //try {
//                        
//
//                        //read a line from the console
//                        lineFromInput = user.getScreenName() 
//                                                  + "," + user.getId()
//                                                  + "," + user.getFollowersCount()
//                                                  + "," + user.getFriendsCount()
//                                                  + "," + user.getDescription()
//                                                  + "," + user.getCreatedAt()
//                                                  + "," + user.getLocation() 
//                                                  + "," + user.isGeoEnabled();
//                                //in.readLine();
////"\t"
//                        //output to the file a line
//                        out.println(lineFromInput);
//                    //TwitterAccountImpl();
//                    //Adding into NEO4J graphic
////                        twit1.setScreenName(user.getScreenName());
////                        twit1.setTwitterId(user.getId());
////                        twit1.setFollowersCount(user.getFollowersCount());
////                        twit1.setFriendsCount(user.getFriendsCount());
////                        twit1.setDesctiption(user.getDescription());
////                        twit1.setCreatedAt(user.getCreatedAt());
////                        twit1.setLocation(user.getLocation());
////                        twit1.setGeoEnabled(user.isGeoEnabled());
//                     
//                     
//                        
//                    //}
//                    //    catch(IOException e1) {
//                    //      System.out.println("Error during reading/writing");
//                    // }
//                      //  System.out.println
//                    } else {
//                        // the user is protected
//                        System.out.println("@" + user.getScreenName());
//                    }
//                }
//                //close the file (VERY IMPORTANT!)
//                
//                page++;
//                System.out.println(page);
//            } while (users.size() != 0 && page < 50);
//            out.close();
//            System.out.println("I'v found " + accountsfound);
//            System.out.println("I'v found user size times the pages: " + (users.size()*page) );
//       
//       
//        }catch(Exception ex){
//            System.out.println(ex.getMessage());
//        }
//        
//    }
    
// CODE NOT USED
//mgr.addPerson(new PersonImpl("Joan Manuel Palos"));
//            mgr.addPerson(new PersonImpl("Kevin D'Arcy"));
//            mgr.addPerson(new PersonImpl("Philip Brgkvist"));
//            mgr.linkPersons(new PersonImpl("Kevin D'Arcy"), new PersonImpl("Joe Bloggs"));
//            mgr.linkPersons(new PersonImpl("Kevin D'Arcy"), new PersonImpl("Joan Manuel Palos"));
//            mgr.linkPersonToEmailAccount(new PersonImpl("Jane Doe"), new EmailAccountImpl("jane@doe.com"));
//            
//            mgr.addTwitterAccount(new TwitterAccountImpl(new Date(), "I am Studying", 13, 82, true,"Aalborg", "Philiptwoshoes", 2730631792L));
//            mgr.linkPersonToTwitterAccount(new PersonImpl("Joe"), new TwitterAccountImpl(new Date(), "desc", 1, 2, true,"loc", "sname", 22));
//            mgr.linkPersonToTwitterAccount(new PersonImpl("Dan"), new TwitterAccountImpl(new Date(), "busy", 3, 4, true,"Barcelona", "sname", 234));
//           //mgr.linkTwitterAccounts(null, null)
//            mgr.linkPersonToTwitterAccount(new PersonImpl("Joe"), new TwitterAccountImpl(new Date(), "desc", 1, 2, true,"Chicago", "sname", 22));
//            mgr.linkPersonToTwitterAccount(new PersonImpl("Dan"), new TwitterAccountImpl(new Date(), "busy", 3, 4, true,"New york", "sname", 234));
//            
//            mgr.addPerson(new PersonImpl("Crispy Brgkvist"));

            // mgr.destroy();