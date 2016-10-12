package krystiannowak.helloworld;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class HelloWorldApplication extends Application<HelloWorldConfiguration> {

	public static void main(String[] args) throws Exception {
		new HelloWorldApplication().run(args);
	}

	@Override
	public String getName() {
		return "hello-world";
	}

	@Override
	public void initialize(Bootstrap<HelloWorldConfiguration> bootstrap) {
	}

	@Override
	public void run(HelloWorldConfiguration configuration, Environment environment) throws Exception {
		environment.jersey().register(new HelloWorldResource());
		environment.healthChecks().register("Hello World", new HelloWorldHealthCheck());
	}

}
