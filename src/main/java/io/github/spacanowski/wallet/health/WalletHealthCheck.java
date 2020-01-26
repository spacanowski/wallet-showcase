package io.github.spacanowski.wallet.health;

import com.codahale.metrics.health.HealthCheck;

public class WalletHealthCheck extends HealthCheck {

    @Override
    protected Result check() throws Exception {
        return Result.healthy();
    }
}
