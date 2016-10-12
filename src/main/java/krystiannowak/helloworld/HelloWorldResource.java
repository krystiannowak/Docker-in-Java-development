package krystiannowak.helloworld;

import java.util.concurrent.atomic.AtomicLong;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/hello-world")
@Produces(MediaType.TEXT_PLAIN)
public class HelloWorldResource {

	private final AtomicLong counter = new AtomicLong(0);

	@GET
	public String hello() {
		return "Hello World! Counter value = " + counter.incrementAndGet();
	}
}
