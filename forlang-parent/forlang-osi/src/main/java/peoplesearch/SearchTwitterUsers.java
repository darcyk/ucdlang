/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peoplesearch;

import ie.ucd.forlang.neo4j.EmbeddedGraphManager;
import ie.ucd.forlang.neo4j.GraphManager;
import ie.ucd.forlang.neo4j.object.Person;
import ie.ucd.forlang.neo4j.object.PersonImpl;
import ie.ucd.forlang.neo4j.object.TwitterAccountImpl;
import java.io.File;
import java.util.Date;
import java.util.List;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.User;
import utils.TwitterLimitWait;

/**
 *
 * @author dm
 */
public class SearchTwitterUsers {
    public void SearchTwitterUsers(){
    int page=1;
    int numberofpages=0;
    Date TwitterAccCreatedAt=new Date();
    String TwitterAccDescr = " ";
    int TwitterFollowersCount = 0;
    int TwitterFriendsCount=0;
    boolean TwitterGeoEnabled=false;
    String TwitterLocation= " ";
    String TwiterAccScrName=" ";
    long TwitterID=0L;
  
    try{

       Twitter twitter = new TwitterFactory().getInstance();
       ResponseList<User> users;
       List<Person> people = null;
       GraphManager mgr = EmbeddedGraphManager.getInstance();
       mgr.init(new File("/usr/local/Cellar/neo4j/2.1.7/libexec/data/forlang1.db"));
       
       mgr.addPerson(new PersonImpl("wgaura"));
       mgr.addPerson(new PersonImpl("Derek Mizak"));
       mgr.addPerson(new PersonImpl("Swiderek"));
       mgr.addPerson(new PersonImpl("Microsoft"));
       mgr.addPerson(new PersonImpl("BBC"));
       mgr.addPerson(new PersonImpl("RTE"));
       mgr.addPerson(new PersonImpl("CNBC"));
       
       people=mgr.listPeople();
       
       for (Person person:people){
       do {
                TwitterLimitWait tlw=new TwitterLimitWait();
                tlw.CheckLimit();
                
                users = twitter.searchUsers(person.getName(), page);
                numberofpages=users.size()/20;
                
                for (User user : users) {
                    if (user.getStatus() != null) {
                        
                        
                        TwitterAccCreatedAt=user.getCreatedAt();
                        if (!user.getDescription().isEmpty()) {TwitterAccDescr=user.getDescription();}
                        //if (user.getFollowersCount()>0) {TwitterFollowersCount=user.getFavouritesCount();}
                        //if  (user.getFriendsCount()>0) {TwitterFriendsCount=user.getFriendsCount();}
                        TwitterFollowersCount=user.getFollowersCount();
                        TwitterFriendsCount=user.getFriendsCount();
                        TwitterGeoEnabled=user.isGeoEnabled();
                        if (!user.getLocation().isEmpty()) {TwitterLocation=user.getLocation();}
                        TwiterAccScrName=user.getScreenName();
                        TwitterID=user.getId();
                        
                        
                        System.out.println("@" + user.getScreenName() + " - " + TwitterFollowersCount + " _ " + TwitterFriendsCount);
                      
                        //mgr.addTwitterAccount(new TwitterAccountImpl(TwitterAccCreatedAt,TwitterAccDescr,TwitterFollowersCount,TwitterFriendsCount,TwitterGeoEnabled,TwitterLocation,TwiterAccScrName,TwitterID));
                        mgr.linkPersonToTwitterAccount(person,new TwitterAccountImpl(TwitterAccCreatedAt,TwitterAccDescr,TwitterFollowersCount,TwitterFriendsCount,TwitterGeoEnabled,TwitterLocation,TwiterAccScrName,TwitterID));
                        
                        TwitterAccDescr=" ";
                        TwitterLocation=" ";
                        TwitterFollowersCount=0;
                        TwitterFriendsCount=0;
                        
                       //numberofusers++;
                       
                    } else {
                        // the user is protected
                        System.out.println("@" + user.getScreenName());
                    }
                }
                
                page++;
                //System.out.println(page);
            } while (users.size() != 0 && page < numberofpages);
       }
        }catch(Exception ex){
            System.out.println(ex.getMessage());
        }
}
    
    public static void main(String[] args) {
        SearchTwitterUsers su=new SearchTwitterUsers();
        su.SearchTwitterUsers();
        
    }
}
