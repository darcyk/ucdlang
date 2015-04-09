package ie.ucd.forlang.neo4j.osi;

import ie.ucd.forlang.neo4j.GraphDatabaseUtils;
import ie.ucd.forlang.neo4j.object.Person;
import ie.ucd.forlang.neo4j.object.PersonImpl;
import ie.ucd.forlang.neo4j.object.RelationshipType;
import ie.ucd.forlang.neo4j.object.TwitterAccount;
import ie.ucd.forlang.neo4j.object.TwitterAccountImpl;

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

	@Description("Get twitter acount owners")
	@PluginTarget(GraphDatabaseService.class)
	public final String getTwitterAccountOwners(
			@Source GraphDatabaseService graphDb,
			@Description("Accounts to explicitly include") @Parameter(name = "includes", optional = true) String[] includes,
			@Description("Accounts to explicitly exclude") @Parameter(name = "excludes", optional = true) String[] excludes) {
		List<TwitterAccount> twitterAccounts = null;
		Twitter twitter = null;
		User user = null;
		long totalPeople = 0;
		try {
			// get all person account objects in neo
			twitterAccounts = GraphDatabaseUtils.listTwitterAccounts(graphDb);
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

	@Description("Get the list of follwers and people following twitter accounts")
	@PluginTarget(GraphDatabaseService.class)
	public final String getTwitterAccountRelationships(
			@Source GraphDatabaseService graphDb,
			@Description("Twitter accounts to explicitly include") @Parameter(name = "includes", optional = true) String[] includes,
			@Description("Twitter accounts to explicitly exclude") @Parameter(name = "excludes", optional = true) String[] excludes) {
		List<TwitterAccount> twitterAccounts = null;
		Twitter twitter = null;
		PagableResponseList<User> users = null;
		TwitterAccount tAccount = null;
		long totalAccounts = 0;
		try {
			// get all twitter account objects in neo
			twitterAccounts = GraphDatabaseUtils.listTwitterAccounts(graphDb);
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

	@Description("Get twitter acounts that Person objects may own")
	@PluginTarget(GraphDatabaseService.class)
	public final String getTwitterAccounts(
			@Source GraphDatabaseService graphDb,
			@Description("People to explicitly include") @Parameter(name = "includes", optional = true) String[] includes,
			@Description("People to explicitly exclude") @Parameter(name = "excludes", optional = true) String[] excludes) {
		List<Person> people = null;
		Twitter twitter = null;
		ResponseList<User> users = null;
		TwitterAccount tAccount = null;
		long totalAccounts = 0;
		try {
			// get all person account objects in neo
			people = GraphDatabaseUtils.listPeople(graphDb);
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