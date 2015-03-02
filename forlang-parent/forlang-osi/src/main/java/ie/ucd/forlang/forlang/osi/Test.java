package ie.ucd.forlang.forlang.osi;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 *
 * @author Kev D'Arcy
 */
public class Test {

    public static final void main(String[] args) {
        try {
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost("http://localhost:8080/workspace0?format=JSON&operation=updateGraph");
            StringEntity body = new StringEntity("{dn: {filter: \"ALL\"}}", ContentType.APPLICATION_JSON);
            post.setEntity(body);
            httpClient.execute(post);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
