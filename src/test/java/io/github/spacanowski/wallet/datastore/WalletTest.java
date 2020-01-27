package io.github.spacanowski.wallet.datastore;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

public class WalletTest {

    @Test
    public void shouldCreateAccount() {
        var wallet = new Wallet();
        var balance = BigDecimal.valueOf(2.2);

        var result = wallet.create(balance);

        assertThat(result.getId(), Matchers.notNullValue());
        assertThat(result.getBalance(), equalTo(balance));
    }

    @Test
    public void shouldGetAccount() {
        var wallet = new Wallet();
        var balance = BigDecimal.valueOf(2.2);

        wallet.create(BigDecimal.valueOf(1.1));

        var account = wallet.create(balance);

        wallet.create(BigDecimal.valueOf(3.3));

        var result = wallet.get(account.getId());

        assertThat(result.getId(), equalTo(account.getId()));
        assertThat(result.getBalance(), equalTo(balance));
    }
}
