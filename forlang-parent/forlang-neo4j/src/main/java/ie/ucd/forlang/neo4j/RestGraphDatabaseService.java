package ie.ucd.forlang.neo4j;

import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.Validate;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.neo4j.collection.primitive.PrimitiveLongIterator;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.QueryExecutionException;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.event.KernelEventHandler;
import org.neo4j.graphdb.event.TransactionEventHandler;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.graphdb.schema.Schema;
import org.neo4j.graphdb.traversal.BidirectionalTraversalDescription;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.helpers.collection.PrefetchingResourceIterator;
import org.neo4j.kernel.api.Statement;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class RestGraphDatabaseService implements GraphDatabaseService {

	public static final String ENCODING = "charset=UTF-8";
	private static final Logger log = Logger.getLogger(RestGraphDatabaseService.class.getName());
	private static final MessageFormat PATH_ADD_LABELS = new MessageFormat("/node/{0}/labels");
	private static final String PATH_ADD_NODE = "/node";
	private static final MessageFormat PATH_NODES_BY_LABEL = new MessageFormat("/label/{0}/nodes");
	private Feature credentials = null;
	private URI uri = null;

	public RestGraphDatabaseService(String uri, String username, String password) {
		super();
		setURI(uri);
		setCredentials(username, password);
		int response = ping();
		if (response != Status.OK.getStatusCode()) {
			throw new IllegalArgumentException("could not connect to database: " + response);
		}
	}

	@Override
	public final Transaction beginTx() {
		throw new UnsupportedOperationException();
	}

	@Override
	public final BidirectionalTraversalDescription bidirectionalTraversalDescription() {
		throw new UnsupportedOperationException();
	}

	@Override
	public final Node createNode() {
		Response response = null;
		try {
			response = executePost(PATH_ADD_NODE, "");
			if (response.getStatus() != Status.CREATED.getStatusCode()) {
				throw new RuntimeException("bad status response from server: " + response.getStatus());
			}
			return parseNode(response.readEntity(String.class));
		}
		catch (Exception e) {
			throw new RuntimeException("could not create node", e);
		}
		finally {
			close(response);
			response = null;
		}
	}

	@Override
	public final Node createNode(Label... labels) {
		Validate.notNull(labels, "labels cannot be null");
		Validate.noNullElements(labels, "labels elements cannot be null");
		Response response = null;
		Node node = null;
		try {
			// create node (cannot create with labels)
			response = executePost(PATH_ADD_NODE, "");
			if (response.getStatus() != Status.CREATED.getStatusCode()) {
				throw new RuntimeException("bad status response from server: " + response.getStatus());
			}
			// parse created node
			node = parseNode(response.readEntity(String.class));
			// update
			response = executePost(PATH_ADD_LABELS.format(node.getId()), labels[0].name());
			if (response.getStatus() != Status.CREATED.getStatusCode()) {
				throw new RuntimeException("bad status response from server: " + response.getStatus());
			}
			node = parseNode(response.readEntity(String.class));
			return node;
		}
		catch (Exception e) {
			throw new RuntimeException("could not create node", e);
		}
		finally {
			close(response);
			response = null;
		}
	}

	@Override
	public final Result execute(String query) throws QueryExecutionException {
		throw new UnsupportedOperationException();
	}

	@Override
	public final Result execute(String query, Map<String, Object> parameters) throws QueryExecutionException {
		throw new UnsupportedOperationException();
	}

	@Override
	public final Node findNode(Label label, String key, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final ResourceIterator<Node> findNodes(Label label) {
		Validate.notNull(label, "label cannot be null");
		Validate.notEmpty(label.toString(), "label must not be empty");
		Response response = null;
		List<Node> nodes = null;
		try {
			response = executeGet(PATH_NODES_BY_LABEL.format(label));
			if (response.getStatus() != Status.OK.getStatusCode()) {
				throw new RuntimeException("bad status response from server: " + response.getStatus());
			}
			nodes = parseNodes(response.readEntity(String.class));
			// return nodes.iterator();
			return new ResourceIterator<Node>() {

				@Override
				public void close() {
				}

				@Override
				public boolean hasNext() {
					return false;
				}

				@Override
				public Node next() {
					return null;
				}

				@Override
				public void remove() {
				}
			};
		}
		catch (Exception e) {
			throw new RuntimeException("could not find nodes by label", e);
		}
		finally {
			close(response);
			response = null;
			nodes = null;
		}
	}

	@Override
	public final ResourceIterator<Node> findNodes(Label label, String key, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final ResourceIterable<Node> findNodesByLabelAndProperty(Label label, String key, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final Iterable<Node> getAllNodes() {
		throw new UnsupportedOperationException();
	}

	@Override
	public final Node getNodeById(long id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final Relationship getRelationshipById(long id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final Iterable<RelationshipType> getRelationshipTypes() {
		throw new UnsupportedOperationException();
	}

	@Override
	public final IndexManager index() {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean isAvailable(long timeout) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final KernelEventHandler registerKernelEventHandler(KernelEventHandler handler) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final <T> TransactionEventHandler<T> registerTransactionEventHandler(TransactionEventHandler<T> handler) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final Schema schema() {
		throw new UnsupportedOperationException();
	}

	@Override
	public final void shutdown() {
		throw new UnsupportedOperationException();
	}

	@Override
	public final TraversalDescription traversalDescription() {
		throw new UnsupportedOperationException();
	}

	@Override
	public final KernelEventHandler unregisterKernelEventHandler(KernelEventHandler handler) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final <T> TransactionEventHandler<T> unregisterTransactionEventHandler(TransactionEventHandler<T> handler) {
		throw new UnsupportedOperationException();
	}

	private final void close(Response response) {
		try {
			if (response != null) {
				response.close();
			}
		}
		catch (Exception e) {
			// swallow
		}
	}

	private final Response executeGet(String path) {
		Validate.notNull(path, "path cannot be null");
		Client client = null;
		try {
			client = ClientBuilder.newClient().register(getCredentials());
			return client.target(getURI()).path(path).request(MediaType.APPLICATION_JSON).acceptEncoding(ENCODING)
					.get();
		}
		catch (Exception e) {
			throw new RuntimeException("could not complete http get", e);
		}
		finally {
			client = null;
		}
	}

	private final Response executePost(String path, String jsonData) {
		Validate.notNull(path, "path cannot be null");
		Validate.notNull(jsonData, "jsonData cannot be null");
		Client client = null;
		try {
			client = ClientBuilder.newClient().register(getCredentials());
			return client.target(getURI()).path(path).request(MediaType.APPLICATION_JSON).acceptEncoding(ENCODING)
					.post(Entity.json(jsonData));
		}
		catch (Exception e) {
			throw new RuntimeException("could not complete http post", e);
		}
		finally {
			client = null;
		}
	}

	private final Feature getCredentials() {
		return credentials;
	}

	private final URI getURI() {
		return uri;
	}

	private final Node parseNode(JsonNode node) {
		Validate.notNull(node, "node cannot be null");
		try {
			return new NodeImpl(node);
		}
		catch (Exception e) {
			throw new RuntimeException("could not parse json to node", e);
		}
	}

	private final Node parseNode(String readEntity) {
		Validate.notEmpty(readEntity, "cannot parse empty json");
		try {
			return new NodeImpl(new ObjectMapper().readTree(readEntity));
		}
		catch (Exception e) {
			throw new RuntimeException("could not parse json to nodes", e);
		}
	}

	private final List<Node> parseNodes(String readEntity) {
		Validate.notEmpty(readEntity, "cannot parse empty json");
		Iterator<JsonNode> it = null;
		List<Node> nodes = null;
		try {
			nodes = new ArrayList<Node>();
			it = new ObjectMapper().readTree(readEntity).elements();
			while (it.hasNext()) {
				nodes.add(parseNode(it.next()));
			}
			return nodes;
		}
		catch (Exception e) {
			throw new RuntimeException("could not parse json to nodes", e);
		}
	}

	private final int ping() {
		Response response = null;
		try {
			response = executeGet("");
			return response.getStatus();
		}
		catch (Exception e) {
			return Status.INTERNAL_SERVER_ERROR.getStatusCode();
		}
		finally {
			close(response);
			response = null;
		}
	}

	private final void setCredentials(String username, String password) {
		Validate.notEmpty(username, "username must have a value");
		Validate.notEmpty(password, "password must have a value");
		credentials = HttpAuthenticationFeature.basic(username, password);
	}

	private final void setURI(String uri) {
		Validate.notEmpty(uri, "uri must have a value");
		try {
			this.uri = new URI(uri);
		}
		catch (Exception e) {
			throw new IllegalArgumentException("bad uri: " + uri, e);
		}
	}
}