package io.github.spacanowski.wallet;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import io.github.spacanowski.wallet.configuration.SimpleWalletConfiguration;
import io.github.spacanowski.wallet.health.WalletHealthCheck;

public class SimpleWalletApplication extends Application<SimpleWalletConfiguration> {

    public static void main(String[] args) throws Exception {
        new SimpleWalletApplication().run(args);
    }

    @Override
    public String getName() {
        return "simple-wallet";
    }

    @Override
    public void run(SimpleWalletConfiguration configuration, Environment environment) throws Exception {
        environment.jersey().packages("io.github.spacanowski.wallet.resources");

        environment.healthChecks().register("health-check", new WalletHealthCheck());
    }
}
