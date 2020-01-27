package io.github.spacanowski.wallet.datastore;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    public void shouldTransferBetweenAccounts() {
        var wallet = new Wallet();

        var fromInitialBalance = BigDecimal.valueOf(2.2);
        var from = wallet.create(fromInitialBalance);

        var toInitialBalance = BigDecimal.valueOf(0.0);
        var to = wallet.create(toInitialBalance);

        var transferSum = BigDecimal.valueOf(1.1);
        wallet.transfer(from.getId(), to.getId(), transferSum);

        var fromAfterTransfer = wallet.get(from.getId());

        assertThat(fromAfterTransfer.getBalance(), equalTo(fromInitialBalance.subtract(transferSum)));

        var toAfterTransfer = wallet.get(to.getId());

        assertThat(toAfterTransfer.getBalance(), equalTo(toInitialBalance.add(transferSum)));
    }

    @Test
    public void shouldNotTransferIfResourcesAreInsuficcient() {
        var wallet = new Wallet();

        var fromInitialBalance = BigDecimal.valueOf(2.2);
        var from = wallet.create(fromInitialBalance);

        var toInitialBalance = BigDecimal.valueOf(0.0);
        var to = wallet.create(toInitialBalance);

        wallet.transfer(from.getId(), to.getId(), fromInitialBalance.multiply(BigDecimal.valueOf(2)));

        var fromAfterTransfer = wallet.get(from.getId());

        assertThat(fromAfterTransfer.getBalance(), equalTo(fromInitialBalance));

        var toAfterTransfer = wallet.get(to.getId());

        assertThat(toAfterTransfer.getBalance(), equalTo(toInitialBalance));
    }

    @Test
    public void shouldNotTransferIfFromIsNotExisting() {
        var wallet = new Wallet();

        var toInitialBalance = BigDecimal.valueOf(0.0);
        var to = wallet.create(toInitialBalance);

        assertThrows(IllegalArgumentException.class,
                     () -> wallet.transfer("123123", to.getId(), BigDecimal.valueOf(2)));

        var toAfterTransfer = wallet.get(to.getId());

        assertThat(toAfterTransfer.getBalance(), equalTo(toInitialBalance));
    }

    @Test
    public void shouldNotTransferIfToIsNotExisting() {
        var wallet = new Wallet();

        var fromInitialBalance = BigDecimal.valueOf(2.2);
        var from = wallet.create(fromInitialBalance);

        assertThrows(IllegalArgumentException.class,
                     () -> wallet.transfer(from.getId(), "123123", BigDecimal.valueOf(2)));

        var fromAfterTransfer = wallet.get(from.getId());

        assertThat(fromAfterTransfer.getBalance(), equalTo(fromInitialBalance));
    }
}
