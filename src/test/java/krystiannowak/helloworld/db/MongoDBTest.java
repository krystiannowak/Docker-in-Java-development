package krystiannowak.helloworld.db;

import static org.junit.Assert.assertTrue;

import org.bson.Document;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

public class MongoDBTest {

	private static final int DEFAULT_MONGO_PORT = ServerAddress.defaultPort();

	@Rule
	public GenericContainer<?> container = new GenericContainer<>("mongo:3.2").withExposedPorts(DEFAULT_MONGO_PORT);

	@Test
	public void test() throws Exception {

		String host = container.getContainerIpAddress();
		int port = container.getMappedPort(DEFAULT_MONGO_PORT);

		MongoClient client = new MongoClient(host, port);
		MongoDatabase database = client.getDatabase(client.listDatabaseNames().first());

		Document buildInfoResults = database.runCommand(new Document("buildInfo", 1));
		assertTrue(buildInfoResults.getString("version").startsWith("3.2."));

		client.close();
	}

}
