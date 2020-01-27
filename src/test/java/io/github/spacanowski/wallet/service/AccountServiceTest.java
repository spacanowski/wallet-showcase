package io.github.spacanowski.wallet.service;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import io.github.spacanowski.wallet.datastore.Wallet;
import io.github.spacanowski.wallet.model.data.Account;
import io.github.spacanowski.wallet.model.input.CreateAccount;

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
}
