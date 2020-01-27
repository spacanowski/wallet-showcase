package io.github.spacanowski.wallet;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import io.github.spacanowski.wallet.configuration.SimpleWalletConfiguration;
import io.github.spacanowski.wallet.datastore.Wallet;
import io.github.spacanowski.wallet.health.WalletHealthCheck;
import io.github.spacanowski.wallet.service.AccountService;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

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
        environment.jersey().packages("io.github.spacanowski.wallet");

        environment.healthChecks().register("health-check", new WalletHealthCheck());

        environment.jersey().register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(AccountService.class).to(AccountService.class);
                bind(Wallet.class).to(Wallet.class);
            }
        });
    }
}
