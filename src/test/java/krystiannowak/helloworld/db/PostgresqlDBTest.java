package krystiannowak.helloworld.db;

import static org.junit.Assert.assertEquals;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;

import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainerProvider;

public class PostgresqlDBTest {

	@Rule
	public JdbcDatabaseContainer<?> container = new PostgreSQLContainerProvider().newInstance("9.6");

	@Test
	public void test() throws Exception {

		Connection conn = container.createConnection("");

		CallableStatement upperProc = conn.prepareCall("{ ? = call upper( ? ) }");
		upperProc.registerOutParameter(1, Types.VARCHAR);
		upperProc.setString(2, "lowercase to uppercase");
		upperProc.execute();
		String upperCased = upperProc.getString(1);
		upperProc.close();

		Thread.sleep(10_1000);

		assertEquals("LOWERCASE TO UPPERCASE", upperCased);
	}

}
