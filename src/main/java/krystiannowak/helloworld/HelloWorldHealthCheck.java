package krystiannowak.helloworld;

import com.codahale.metrics.health.HealthCheck;

public class HelloWorldHealthCheck extends HealthCheck {

	@Override
	protected Result check() throws Exception {
		// TODO: normally do some more fancy and realistic check
		return Result.healthy();
	}

}
