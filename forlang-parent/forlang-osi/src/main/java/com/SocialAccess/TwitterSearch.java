/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.SocialAccess;

import java.util.List;
import peoplesearch.CSVutil;
import peoplesearch.Constants;
import twitter4j.*;
//import twitter4j.Twitter;
//import twitter4j.TwitterException;
//import twitter4j.TwitterFactory;
//import twitter4j.Friendship;
//import twitter4j.Relationship;
//import twitter4j.ResponseList;


/**
 *
 * @author dm
 */
public class TwitterSearch{
public static String[] Analyse(String[] peopleTwitter) {
    String[] returnTemp = new String[2];
    
    returnTemp=peopleTwitter;
    
    System.out.println("People to be analysed in Twitter are: "+ returnTemp[0]+ " and "+returnTemp[1]);
    
return returnTemp;   
}

public static void searchWhomUserFollow(){

    List<String[]>data=CSVutil.readCSV(Constants.CSV_FILE_NAME);
    String[]people=data.get(0);
    System.out.print(people[0]);
    
    try {
            Twitter twitter = new TwitterFactory().getInstance();
            long cursor = -1;
            IDs ids;
            System.out.println("Listing following ids.");
            do {
                
                    ids = twitter.getFriendsIDs(people[0], cursor);
                
                for (long id : ids.getIDs()) {
                    System.out.println(id);
                }
            } while ((cursor = ids.getNextCursor()) != 0);
            System.exit(0);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to get friends' ids: " + te.getMessage());
            System.exit(-1);
        }
}

public static void searchWhoFollowUser(){
    
    
}

public static void ShowFriendsListNames(){
    List<String[]>data=CSVutil.readCSV(Constants.CSV_FILE_NAME);
    String[]people=data.get(0);
try {
            Twitter twitter = new TwitterFactory().getInstance();
            ResponseList<Friendship> friendships = twitter.lookupFriendships(people);
            for (Friendship friendship : friendships) {
                System.out.println("@" + friendship.getScreenName()
                        + " following: " + friendship.isFollowing()
                        + " followed_by: " + friendship.isFollowedBy());
            }
            System.out.println("Successfully looked up friendships [" + people[0] + "].");
            System.exit(0);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to lookup friendships: " + te.getMessage());
            System.exit(-1);
        }
}
public static String[] FriendshipStatus(String[] args){
    
    if (args.length < 2) {
            System.out.println("Usage: [source screen name] [target screen name]");
            System.exit(-1);
        }
    
    String[] lineConnection = new String[2];
        try {
            Twitter twitter = new TwitterFactory().getInstance();
            Relationship relationship = twitter.showFriendship(args[0], args[1]);
            
            lineConnection[0]=args[0]+" is followed by "+ args[1]+" "+relationship.isSourceFollowedByTarget();
            lineConnection[1]=args[0]+" is following "+ args[1]+" "+relationship.isSourceFollowingTarget();
            
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to show friendship: " + te.getMessage());
            System.exit(-1);
        }
return lineConnection;
}
public static void main(String[]args){
    //TwitterSearch.searchWhomUserFollow();
    TwitterSearch.ShowFriendsListNames();
    
}
}
