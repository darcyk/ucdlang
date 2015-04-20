package ie.ucd.forlang.neo4j.osi;

import ie.ucd.forlang.neo4j.GraphDatabaseUtils;
import ie.ucd.forlang.neo4j.object.Person;
import ie.ucd.forlang.neo4j.object.PersonImpl;
import ie.ucd.forlang.neo4j.object.RelationshipType;
import ie.ucd.forlang.neo4j.object.TwitterAccount;
import ie.ucd.forlang.neo4j.object.TwitterAccountImpl;

import java.util.Iterator;
import java.util.List;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.server.plugins.Description;
import org.neo4j.server.plugins.Parameter;
import org.neo4j.server.plugins.PluginTarget;
import org.neo4j.server.plugins.ServerPlugin;
import org.neo4j.server.plugins.Source;

import twitter4j.PagableResponseList;
import twitter4j.RateLimitStatus;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.User;

@Description("An extension to the Neo4j Server for getting potential Twitter acounts for Person objects and its friends and followers")
public final class TwitterUsers extends ServerPlugin {

	public static final String[] PACERS = { "/friends/list", "/users/search", "/users/show/:id" };

	@Description("Get a list of all of the TwitterAccounts objects from the database and add their owners (Person objects)")
	@PluginTarget(GraphDatabaseService.class)
	public final String getTwitterAccountOwners(@Source GraphDatabaseService graphDb,
			@Description("Twitter accounts to explicitly include in the search, remove all others") @Parameter(name = "includes", optional = true) String[] includes,
			@Description("Twitter accounts to explicitly exclude in the search, include all others") @Parameter(name = "excludes", optional = true) String[] excludes) {
		List<TwitterAccount> twitterAccounts = null;
		Twitter twitter = null;
		User user = null;
		long totalPeople = 0;
		try {
			// get all person account objects in neo
			twitterAccounts = GraphDatabaseUtils.listTwitterAccounts(graphDb);
			applyIncludesTwitter(includes, twitterAccounts);
			applyExcludesTwitter(excludes, twitterAccounts);
			// go though each person object and find owners
			twitter = TwitterFactory.getSingleton();
			for (TwitterAccount twitterAccount : twitterAccounts) {
				pause(twitter);
				user = twitter.showUser(twitterAccount.getTwitterId());
				GraphDatabaseUtils.linkPersonToTwitterAccount(graphDb, new PersonImpl(user.getName()), twitterAccount,
						RelationshipType.OWNS);
				totalPeople++;
				user = null;
			}
			return "total people added: " + totalPeople;
		}
		catch (Exception e) {
			return "could not complete people lookup, added " + totalPeople + " people. " + e.toString();
		}
		finally {
			twitterAccounts = null;
			twitter = null;
			user = null;
		}
	}

	@Description("Get a list of all of the TwitterAccounts objects from the database and add their friends and followers")
	@PluginTarget(GraphDatabaseService.class)
	public final String getTwitterAccountRelationships(
			@Source GraphDatabaseService graphDb,
			@Description("Twitter accounts to explicitly include in the search, remove all others") @Parameter(name = "includes", optional = true) String[] includes,
			@Description("Twitter accounts to explicitly exclude in the search, include all others") @Parameter(name = "excludes", optional = true) String[] excludes) {
		List<TwitterAccount> twitterAccounts = null;
		Twitter twitter = null;
		PagableResponseList<User> users = null;
		TwitterAccount tAccount = null;
		long totalAccounts = 0;
		try {
			// get all twitter account objects in neo
			twitterAccounts = GraphDatabaseUtils.listTwitterAccounts(graphDb);
			applyIncludesTwitter(includes, twitterAccounts);
			applyExcludesTwitter(excludes, twitterAccounts);
			// go though each twitter account object and find its relationships
			twitter = TwitterFactory.getSingleton();
			long cursor = -1;
			for (TwitterAccount twitterAccount : twitterAccounts) {
				cursor = -1;
				// followers list
				while (cursor != 0) {
					pause(twitter);
					users = twitter.getFollowersList(twitterAccount.getTwitterId(), cursor);
					for (User user : users) {
						tAccount = new TwitterAccountImpl(user.getCreatedAt(), user.getDescription(),
								user.getFollowersCount(), user.getFriendsCount(), user.isGeoEnabled(),
								user.getLocation(), user.getScreenName(), user.getId());
						// GraphDatabaseUtils.linkPersonToTwitterAccount(graphDb, new PersonImpl(user.getName()),
						// tAccount, RelationshipType.OWNS);
						GraphDatabaseUtils.linkTwitterAccounts(graphDb, twitterAccount, tAccount,
								RelationshipType.IS_FOLLOWED_BY);
						totalAccounts++;
						tAccount = null;
						user = null;
					}
					cursor = users.getNextCursor();
				}
				cursor = -1;
				// following list
				while (cursor != 0) {
					pause(twitter);
					users = twitter.getFriendsList(twitterAccount.getTwitterId(), cursor);
					for (User user : users) {
						tAccount = new TwitterAccountImpl(user.getCreatedAt(), user.getDescription(),
								user.getFollowersCount(), user.getFriendsCount(), user.isGeoEnabled(),
								user.getLocation(), user.getScreenName(), user.getId());
						// GraphDatabaseUtils.linkPersonToTwitterAccount(graphDb, new PersonImpl(user.getName()),
						// tAccount, RelationshipType.OWNS);
						GraphDatabaseUtils.linkTwitterAccounts(graphDb, twitterAccount, tAccount,
								RelationshipType.FOLLOWS);
						totalAccounts++;
						tAccount = null;
						user = null;
					}
					cursor = users.getNextCursor();
				}
				twitterAccount = null;
			}
			return "total followers added: " + totalAccounts;
		}
		catch (Exception e) {
			return "could not complete twitter relationships lookup, added " + totalAccounts + " followers. "
					+ e.toString();
		}
		finally {
			twitterAccounts = null;
			twitter = null;
			users = null;
			tAccount = null;
		}
	}

	@Description("Get a list of all of the Person objects from the database and search for Twitter accounts that they may own")
	@PluginTarget(GraphDatabaseService.class)
	public final String getTwitterAccounts(
			@Source GraphDatabaseService graphDb,
			@Description("People to explicitly include in the search, remove all others") @Parameter(name = "includes", optional = true) String[] includes,
			@Description("People to explicitly exclude in the search, include all others") @Parameter(name = "excludes", optional = true) String[] excludes) {
		List<Person> people = null;
		Twitter twitter = null;
		ResponseList<User> users = null;
		TwitterAccount tAccount = null;
		long totalAccounts = 0;
		try {
			// get all person account objects in neo
			people = GraphDatabaseUtils.listPeople(graphDb);
			applyIncludesPeople(includes, people);
			applyExcludesPeople(excludes, people);
			// go though each person object and find potential twitter accounts
			twitter = TwitterFactory.getSingleton();
			int page = 1;
			for (Person person : people) {
				pause(twitter);
				users = twitter.searchUsers(person.getName(), page);
				for (User user : users) {
					tAccount = new TwitterAccountImpl(user.getCreatedAt(), user.getDescription(),
							user.getFollowersCount(), user.getFriendsCount(), user.isGeoEnabled(), user.getLocation(),
							user.getScreenName(), user.getId());
					GraphDatabaseUtils.linkPersonToTwitterAccount(graphDb, person, tAccount,
							RelationshipType.PROBABLY_OWNS);
					totalAccounts++;
					tAccount = null;
					user = null;
				}
				page++;
				person = null;
			}
			return "total accounts added: " + totalAccounts;
		}
		catch (Exception e) {
			return "could not complete twitter accounts lookup, added " + totalAccounts + " accounts. " + e.toString();
		}
		finally {
			people = null;
			twitter = null;
			users = null;
			tAccount = null;
		}
	}

	/**
	 * Apply the excludes rule: remove each person that is in the array from the person list
	 * 
	 * @param includes String[] The array of person names to remove
	 * @param people List<Person> The list from which to remove them
	 */
	private final void applyExcludesPeople(String[] excludes, List<Person> people) {
		if (people != null && excludes != null && excludes.length > 0) {
			for (int i = 0; i < excludes.length; i++) {
				Iterator<Person> it = people.listIterator();
				while (it.hasNext()) {
					Person p = it.next();
					if (p.getName().equals(excludes[i])) {
						it.remove();
					}
				}
			}
		}
	}

	/**
	 * Apply the excludes rule: remove each twitter account that is in the array from the twitter account list
	 * 
	 * @param includes String[] The array of twitter account names to remove
	 * @param Accouts List<TwitterAccount> The list from which to remove them
	 */
	private final void applyExcludesTwitter(String[] excludes, List<TwitterAccount> accounts) {
		if (accounts != null && excludes != null && excludes.length > 0) {
			for (int i = 0; i < excludes.length; i++) {
				Iterator<TwitterAccount> it = accounts.listIterator();
				while (it.hasNext()) {
					TwitterAccount p = it.next();
					if (p.getScreenName().equals(excludes[i])) {
						it.remove();
					}
				}
			}
		}
	}

	/**
	 * Apply the includes rule: remove each person that is in not the array from the person list
	 * 
	 * @param includes String[] The array of person names to keep
	 * @param people List<Person> The list from which to remove them
	 */
	private final void applyIncludesPeople(String[] includes, List<Person> people) {
		if (people != null && includes != null && includes.length > 0) {
			for (int i = 0; i < includes.length; i++) {
				Iterator<Person> it = people.listIterator();
				while (it.hasNext()) {
					Person p = it.next();
					if (!p.getName().equals(includes[i])) {
						it.remove();
					}
				}
			}
		}
	}

	/**
	 * Apply the includes rule: remove each twitter account that is in not the array from the twitter account list
	 * 
	 * @param includes String[] The array of twitter account names to keep
	 * @param people List<TwitterAccount> The list from which to remove them
	 */
	private final void applyIncludesTwitter(String[] includes, List<TwitterAccount> accounts) {
		if (accounts != null && includes != null && includes.length > 0) {
			for (int i = 0; i < includes.length; i++) {
				Iterator<TwitterAccount> it = accounts.listIterator();
				while (it.hasNext()) {
					TwitterAccount p = it.next();
					if (!p.getScreenName().equals(includes[i])) {
						it.remove();
					}
				}
			}
		}
	}

	private final void pause(Twitter twitter) {
		RateLimitStatus status = null;
		try {
			for (int i = 0; i < PACERS.length; i++) {
				status = twitter.getRateLimitStatus().get(PACERS[i]);
				// System.out.println(PACERS[i] + ":" + status);
				if (status.getRemaining() <= 1) {
					// System.out.println("sleeping for " + status.getSecondsUntilReset() + " seconds");
					Thread.sleep(status.getSecondsUntilReset() * 1000);
				}
			}
		}
		catch (Exception e) {
			// swallow
		}
		finally {
			status = null;
		}
	}
}