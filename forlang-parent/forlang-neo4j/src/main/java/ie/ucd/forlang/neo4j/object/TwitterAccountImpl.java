package ie.ucd.forlang.neo4j.object;

import ie.ucd.forlang.neo4j.Constants;

import java.util.Date;

import org.apache.commons.lang3.Validate;
import org.neo4j.graphdb.Node;

public class TwitterAccountImpl extends GraphObjectImpl implements TwitterAccount {

	public TwitterAccountImpl() {
		super();
	}

	public TwitterAccountImpl(Date createdAt, String description, int followersCount, int friendsCount,
			boolean geoEnabled, String location, String screenName, long twitterId) {
		super();
		setCreatedAt(createdAt);
		setDesctiption(description);
		setFollowersCount(followersCount);
		setFriendsCount(friendsCount);
		setGeoEnabled(geoEnabled);
		setLocation(location);
		setScreenName(screenName);
		setTwitterId(twitterId);
	}

	public TwitterAccountImpl(Node node) {
		super(node);
		setCreatedAt(new Date((Long) node.getProperty(Constants.PROP_TWITTER_CREATED_AT)));
		setDesctiption((String) node.getProperty(Constants.PROP_TWITTER_DESCRIPTION));
		setFollowersCount((int) node.getProperty(Constants.PROP_TWITTER_FOLLOWERS_COUNT));
		setFriendsCount((int) node.getProperty(Constants.PROP_TWITTER_FRIENDS_COUNT));
		setGeoEnabled((boolean) node.getProperty(Constants.PROP_TWITTER_GEO_ENABLED));
		setLocation((String) node.getProperty(Constants.PROP_TWITTER_LOCATION));
		setScreenName((String) node.getProperty(Constants.PROP_TWITTER_SCREEN_NAME));
		setTwitterId((long) node.getProperty(Constants.PROP_TWITTER_ID));
	}

	/** @see TwitterAccount#getCreatedAt() */
	@Override
	public final Date getCreatedAt() {
		return new Date((Long) getProperty(Constants.PROP_TWITTER_CREATED_AT));
	}

	/** @see TwitterAccount#getDescription() */
	@Override
	public final String getDescription() {
		return (String) getProperty(Constants.PROP_TWITTER_DESCRIPTION);
	}

	/** @see TwitterAccount#getFollowersCount() */
	@Override
	public final int getFollowersCount() {
		return (int) getProperty(Constants.PROP_TWITTER_FOLLOWERS_COUNT);
	}

	/** @see TwitterAccount#getFriendsCount() */
	@Override
	public final int getFriendsCount() {
		return (int) getProperty(Constants.PROP_TWITTER_FRIENDS_COUNT);
	}

	/** @see GraphObject#getGraphObjectType() */
	@Override
	public final GraphObjectType getGraphObjectType() {
		return GraphObjectType.TwitterAccount;
	}

	/** @see TwitterAccount#getLocation() */
	@Override
	public final String getLocation() {
		return (String) getProperty(Constants.PROP_TWITTER_LOCATION);
	}

	/** @see GraphObject#getPrimaryPropertyName() */
	@Override
	public final String getPrimaryPropertyName() {
		return Constants.PROP_TWITTER_SCREEN_NAME;
	}

	/** @see GraphObject#getPrimaryPropertyValue() */
	@Override
	public final Object getPrimaryPropertyValue() {
		return getScreenName();
	}

	/** @see TwitterAccount#getScreenName() */
	@Override
	public final String getScreenName() {
		return (String) getProperty(Constants.PROP_TWITTER_SCREEN_NAME);
	}

	/** @see TwitterAccount#getTwitterId() */
	@Override
	public final long getTwitterId() {
		return (long) getProperty(Constants.PROP_TWITTER_ID);
	}

	/** @see TwitterAccount#isGeoEnabled() */
	@Override
	public final boolean isGeoEnabled() {
		return (boolean) getProperty(Constants.PROP_TWITTER_GEO_ENABLED);
	}

	/** @see TwitterAccount#setCreatedAt(Date) */
	@Override
	public final void setCreatedAt(Date createdAt) {
		Validate.notNull(createdAt, "createdAt cannot be null");
		setProperty(Constants.PROP_TWITTER_CREATED_AT, createdAt.getTime());
	}

	/** @see TwitterAccount#setDesctiption(String) */
	@Override
	public final void setDesctiption(String description) {
		//Validate.notNull(description, "description cannot be null");
		//Validate.notEmpty(description, "description must have a value");
		setProperty(Constants.PROP_TWITTER_DESCRIPTION, description);
	}

	/** @see TwitterAccount#setFollowersCount(int) */
	@Override
	public final void setFollowersCount(int followerCount) {
		setProperty(Constants.PROP_TWITTER_FOLLOWERS_COUNT, followerCount);
	}

	/** @see TwitterAccount#setFriendsCount(int) */
	@Override
	public final void setFriendsCount(int friendsCount) {
		setProperty(Constants.PROP_TWITTER_FRIENDS_COUNT, friendsCount);
	}

	/** @see TwitterAccount#setGeoEnabled(boolean) */
	@Override
	public final void setGeoEnabled(boolean geoEnabled) {
		setProperty(Constants.PROP_TWITTER_GEO_ENABLED, geoEnabled);
	}

	/** @see TwitterAccount#setLocation(String) */
	@Override
	public final void setLocation(String location) {
		//Validate.notNull(location, "location cannot be null");
		//Validate.notEmpty(location, "location must have a value");
		setProperty(Constants.PROP_TWITTER_LOCATION, location);
	}

	/** @see TwitterAccount#setScreenName(String) */
	@Override
	public final void setScreenName(String screenName) {
		Validate.notNull(screenName, "screenName cannot be null");
		Validate.notEmpty(screenName, "screenName must have a value");
		setProperty(Constants.PROP_TWITTER_SCREEN_NAME, screenName);
	}

	/** @see TwitterAccount#setTwitterId(long) */
	@Override
	public final void setTwitterId(long id) {
		setProperty(Constants.PROP_TWITTER_ID, id);
	}
}