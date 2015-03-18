package ie.ucd.forlang.neo4j.object;

import java.util.Date;

/**
 * Object to encapsulate a twitter account
 * 
 * @author Kev D'Arcy
 */
public interface TwitterAccount {

	/**
	 * Returns when the account was created
	 *
	 * @return Date When the account was created
	 */
	public Date getCreatedAt();

	/**
	 * Returns the description of the user
	 *
	 * @return String The description of the user
	 */
	public String getDescription();

	/**
	 * Returns the number of followers
	 *
	 * @return int The number of followers
	 */
	public int getFollowersCount();

	/**
	 * Returns the number of users the user follows (AKA "followings")
	 *
	 * @return int The number of users the user follows
	 */
	public int getFriendsCount();

	/**
	 * Returns the location of the user
	 *
	 * @return String The location of the user
	 */
	public String getLocation();

	/**
	 * Returns the screen name of the user
	 *
	 * @return String The screen name of the user
	 */
	public String getScreenName();

	/**
	 * Returns the twitter id of the user
	 *
	 * @return long The twitter id of the user
	 */
	public long getTwitterId();

	/**
	 * Returns whether the user is enabling geo location
	 * 
	 * @return boolean If the user is enabling geo location
	 */
	public boolean isGeoEnabled();

	/**
	 * Set when the account was created
	 *
	 * @param Date When the account was created
	 */
	public void setCreatedAt(Date createdAt);

	/**
	 * Set the description of the user
	 *
	 * @param String The description of the user
	 */
	public void setDesctiption(String description);

	/**
	 * Set the number of followers
	 *
	 * @param int The number of followers
	 */
	public void setFollowersCount(int followerCount);

	/**
	 * Set the number of users the user follows (AKA "followings")
	 *
	 * @param int The number of users the user follows
	 */
	public void setFriendsCount(int friendsCount);

	/**
	 * Set whether the user is enabling geo location
	 * 
	 * @param boolean If the user is enabling geo location
	 */
	public void setGeoEnabled(boolean geoEnabled);

	/**
	 * Set the location of the user
	 *
	 * @param String The location of the user
	 */
	public void setLocation(String location);

	/**
	 * Set the screen name of the user
	 *
	 * @param String The screen name of the user
	 */
	public void setScreenName(String screenName);

	/**
	 * Set the twitter id of the user
	 *
	 * @param long The twitter id of the user
	 */
	public void setTwitterId(long twitterId);
}