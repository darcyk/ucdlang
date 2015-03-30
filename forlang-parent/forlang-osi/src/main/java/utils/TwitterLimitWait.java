
package utils;

import java.util.Map;
import twitter4j.RateLimitStatus;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

/**
 *
 * @author dm
 */
public class TwitterLimitWait {
    
    public static long CheckLimit(){
        long StatusUntilReset=1;
        try {
            Twitter twitter = new TwitterFactory().getInstance();
            Map<String ,RateLimitStatus> rateLimitStatus = twitter.getRateLimitStatus();
            for (String endpoint : rateLimitStatus.keySet()) {
                RateLimitStatus status = rateLimitStatus.get(endpoint);
                //System.out.println("Endpoint: " + endpoint);
                //System.out.println(" Limit: " + status.getLimit());
                //System.out.println(" Remaining: " + status.getRemaining());
                if(status.getRemaining()<5){
                                            StatusUntilReset=status.getSecondsUntilReset();
                                            System.out.println("I have to wait for twiiter"+ " "+
                                                    StatusUntilReset);
                                            Thread.sleep(StatusUntilReset);
                                            };
                //System.out.println(" ResetTimeInSeconds: " + status.getResetTimeInSeconds());
                //System.out.println(" SecondsUntilReset: " + status.getSecondsUntilReset());
                
            }
            
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to get rate limit status: " + e.getMessage());
            System.exit(-1);
        }
    
    
    return StatusUntilReset;
    }
 public static void main(String[] args){
     TwitterLimitWait tlw=new TwitterLimitWait();
     tlw.CheckLimit();
 }   
}
