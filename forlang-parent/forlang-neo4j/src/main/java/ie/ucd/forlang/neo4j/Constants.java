package ie.ucd.forlang.neo4j;

public final class Constants {

	// default values
	public static final long DEF_OBJECT_ID = -1;
	// property names
	public static final String PROP_DATE_SENT = "dateSent";
	public static final String PROP_EMAIL_ADDRESS = "emailAddress";
	public static final String PROP_NAME = "name";
	public static final String PROP_RECIPIENTS = "recipients";
	public static final String PROP_SENDER = "sender";
	public static final String PROP_SUBJECT = "subject";
	public static final String PROP_TWITTER_CREATED_AT = "createdAt";
	public static final String PROP_TWITTER_DESCRIPTION = "description";
	public static final String PROP_TWITTER_FOLLOWERS_COUNT = "followersCount";
	public static final String PROP_TWITTER_FRIENDS_COUNT = "friendsCount";
	public static final String PROP_TWITTER_GEO_ENABLED = "geoEnabled";
	public static final String PROP_TWITTER_ID = "twitterId";
	public static final String PROP_TWITTER_LOCATION = "location";
	public static final String PROP_TWITTER_SCREEN_NAME = "screenName";

	// make sure you can't instantiate
	private Constants() {
	}
}