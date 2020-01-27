package io.github.spacanowski.wallet.service;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import io.github.spacanowski.wallet.datastore.Wallet;
import io.github.spacanowski.wallet.model.data.Account;
import io.github.spacanowski.wallet.model.input.CreateAccount;
import io.github.spacanowski.wallet.model.input.Transfer;

import java.math.BigDecimal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class AccountServiceTest {

    private static final Wallet wallet = mock(Wallet.class);

    private static final AccountService accountService = new AccountService(wallet);

    @AfterEach
    public void tearDown(){
        reset(wallet);
    }

    @Test
    public void shouldCreateAccount() {
        var accountId = "1-1-1";
        var balance = BigDecimal.valueOf(1.1);

        var createAccount = new CreateAccount();
        createAccount.setBalance(balance);

        var account = new Account();
        account.setId(accountId);
        account.setBalance(balance);

        when(wallet.create(eq(balance)))
        .thenReturn(account);

        var result = accountService.createAccount(createAccount);

        assertThat(result.getId(), equalTo(accountId));
        assertThat(result.getBalance(), equalTo(balance));
    }

    @Test
    public void shouldGetAccount() {
        var accountId = "1-1-1";
        var balance = BigDecimal.valueOf(1.1);

        var createAccount = new CreateAccount();
        createAccount.setBalance(balance);

        var account = new Account();
        account.setId(accountId);
        account.setBalance(balance);

        when(wallet.get(eq(accountId)))
        .thenReturn(account);

        var result = accountService.getAccount(accountId);

        assertThat(result.getId(), equalTo(accountId));
        assertThat(result.getBalance(), equalTo(balance));
    }

    @Test
    public void shouldTransferBetweenAccounts() {
        var fromInitialBalance = BigDecimal.valueOf(2.2);
        var from = new Account();
        var fromId = "1-1-1";
        from.setId(fromId);
        from.setBalance(fromInitialBalance);

        var toInitialBalance = BigDecimal.valueOf(0.0);

        var to = new Account();
        var toId = "2-2-2";
        to.setId(toId);
        to.setBalance(toInitialBalance);

        when(wallet.get(eq(from.getId())))
        .thenReturn(from);

        when(wallet.get(eq(to.getId())))
        .thenReturn(to);

        var transferSum = BigDecimal.valueOf(1.1);
        var transfer = new Transfer();
        transfer.setSum(transferSum);

        var result = accountService.transferResources(from.getId(), to.getId(), transfer);

        assertThat(result.getFrom().getId(), equalTo(fromId));
        assertThat(result.getFrom().getBalance(), equalTo(fromInitialBalance));

        assertThat(result.getTo().getId(), equalTo(toId));
        assertThat(result.getTo().getBalance(), equalTo(toInitialBalance));
    }

    @Test
    public void shouldNotTransferToSameAccount() {
        var fromId = "1-1-1";

        var transfer = new Transfer();
        transfer.setSum(BigDecimal.valueOf(1.1));

        assertThrows(IllegalArgumentException.class,
                     () -> accountService.transferResources(fromId, fromId, transfer));
    }
}
