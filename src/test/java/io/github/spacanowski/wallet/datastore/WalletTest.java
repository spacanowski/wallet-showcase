package io.github.spacanowski.wallet.datastore;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.spacanowski.wallet.exception.AccountNotFoundException;

import java.math.BigDecimal;
import java.util.stream.IntStream;

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

        assertThrows(AccountNotFoundException.class,
                     () -> wallet.transfer("123123", to.getId(), BigDecimal.valueOf(2)));

        var toAfterTransfer = wallet.get(to.getId());

        assertThat(toAfterTransfer.getBalance(), equalTo(toInitialBalance));
    }

    @Test
    public void shouldNotTransferIfToIsNotExisting() {
        var wallet = new Wallet();

        var fromInitialBalance = BigDecimal.valueOf(2.2);
        var from = wallet.create(fromInitialBalance);

        assertThrows(AccountNotFoundException.class,
                     () -> wallet.transfer(from.getId(), "123123", BigDecimal.valueOf(2)));

        var fromAfterTransfer = wallet.get(from.getId());

        assertThat(fromAfterTransfer.getBalance(), equalTo(fromInitialBalance));
    }

    @Test
    public void shouldDeleteAccount() {
        var wallet = new Wallet();
        var balance = BigDecimal.valueOf(2.2);

        var account = wallet.create(balance);

        assertNotNull(wallet.get(account.getId()));

        wallet.delete(account.getId());

        assertNull(wallet.get(account.getId()));
    }

    @Test
    public void shouldTransferBetweenAccountsInThreadSafeWay() {
        var wallet = new Wallet();

        var account1InitialBalance = BigDecimal.valueOf(1000.0);
        var account1 = wallet.create(account1InitialBalance);

        var account2InitialBalance = BigDecimal.valueOf(1000.0);
        var account2 = wallet.create(account2InitialBalance);

        var account1toAccount2Sum = BigDecimal.valueOf(1.1);
        var account2toAccount1Sum = BigDecimal.valueOf(2.2);

        var operationsCount = BigDecimal.valueOf(100);

        IntStream.range(1, operationsCount.intValue() * 2 + 1)
                 .parallel()
                 .forEach(i -> {
                     if ((i % 2) == 0) {
                         wallet.transfer(account1.getId(), account2.getId(), account1toAccount2Sum);
                     } else {
                         wallet.transfer(account2.getId(), account1.getId(), account2toAccount1Sum);
                     }
                 });

        var account1AfterTransfer = wallet.get(account1.getId());

        assertThat(account1AfterTransfer.getBalance(),
                   equalTo(account1InitialBalance
                                 .add(account2toAccount1Sum.multiply(operationsCount))
                                 .subtract(account1toAccount2Sum.multiply(operationsCount))));

        var account2AfterTransfer = wallet.get(account2.getId());

        assertThat(account2AfterTransfer.getBalance(),
                   equalTo(account2InitialBalance
                                .add(account1toAccount2Sum.multiply(operationsCount))
                                .subtract(account2toAccount1Sum.multiply(operationsCount))));
    }
}
