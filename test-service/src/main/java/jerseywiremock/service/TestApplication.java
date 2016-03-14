package jerseywiremock.service;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

public class TestApplication extends Application<TestConfiguration> {
    public static void main(String[] args) throws Exception {
        new TestApplication().run(args);
    }

    @Override
    public void run(TestConfiguration testConfiguration, Environment environment) throws Exception {

    }
}
