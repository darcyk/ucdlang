package ie.ucd.forlang.neo4j;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

public class TestUtils {

	public static final void clearDatabase(String uri, String username, String password) {
		Client client = null;
		Response response = null;
		String json = null;
		try {
			json = "{ \"statements\" : [ { \"statement\" : \"MATCH (n) OPTIONAL MATCH (n)-[r]-() DELETE n,r\" } ] }";
			client = ClientBuilder.newClient().register(HttpAuthenticationFeature.basic(username, password));
			response = client.target(uri).path("/transaction/commit").request(MediaType.APPLICATION_JSON)
					.acceptEncoding("charset=UTF-8").post(Entity.json(json));
			if (response.getStatus() != Status.OK.getStatusCode()) {
				throw new RuntimeException("bad status response from server: " + response.getStatus());
			}
			System.out.println(response.readEntity(String.class));
		}
		catch (Exception e) {
			throw new RuntimeException("could not reset server", e);
		}
		finally {
			response.close();
			response = null;
			client = null;
			json = null;
		}
	}
}