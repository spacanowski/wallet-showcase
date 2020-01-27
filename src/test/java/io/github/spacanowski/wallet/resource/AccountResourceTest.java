package io.github.spacanowski.wallet.resource;

import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import io.github.spacanowski.wallet.exception.AccountNotFoundException;
import io.github.spacanowski.wallet.model.input.CreateAccount;
import io.github.spacanowski.wallet.model.input.Transfer;
import io.github.spacanowski.wallet.model.output.AccountOutput;
import io.github.spacanowski.wallet.model.output.TransferOutput;
import io.github.spacanowski.wallet.resource.providers.AccountNotFoundExceptionMapper;
import io.github.spacanowski.wallet.resource.providers.IllegalArgumentExceptionMapper;
import io.github.spacanowski.wallet.service.AccountService;

import java.math.BigDecimal;

import org.junit.ClassRule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(DropwizardExtensionsSupport.class)
public class AccountResourceTest {

    private static final AccountService accountService = mock(AccountService.class);

    @ClassRule
    public static final ResourceExtension resource = ResourceExtension.builder()
                                                                      .addResource(new AccountResource(accountService))
                                                                      .addProvider(new IllegalArgumentExceptionMapper())
                                                                      .addProvider(new AccountNotFoundExceptionMapper())
                                                                      .build();

    @AfterEach
    public void tearDown(){
        reset(accountService);
    }

    @Test
    public void shouldCreateAccount() {
        var accountId = "1-1-1";
        var balance = BigDecimal.valueOf(1.1);

        var createAccount = new CreateAccount();
        createAccount.setBalance(balance);

        when(accountService.createAccount(eq(createAccount)))
        .thenReturn(new AccountOutput(accountId, balance));

        var response = resource.target("/accounts")
                               .request()
                               .post(entity(createAccount, APPLICATION_JSON));

        assertThat(response.getStatus(), equalTo(CREATED.getStatusCode()));

        var result = response.readEntity(AccountOutput.class);

        assertThat(result.getId(), equalTo(accountId));
        assertThat(result.getBalance(), equalTo(balance));

        verify(accountService).createAccount(createAccount);
    }

    @Test
    public void shouldCreateAccountWithZeroResources() {
        var accountId = "1-1-1";
        var balance = BigDecimal.valueOf(0.0);

        var createAccount = new CreateAccount();
        createAccount.setBalance(balance);

        when(accountService.createAccount(eq(createAccount)))
        .thenReturn(new AccountOutput(accountId, balance));

        var response = resource.target("/accounts")
                               .request()
                               .post(entity(createAccount, APPLICATION_JSON));

        assertThat(response.getStatus(), equalTo(CREATED.getStatusCode()));

        var result = response.readEntity(AccountOutput.class);

        assertThat(result.getId(), equalTo(accountId));
        assertThat(result.getBalance(), equalTo(balance));

        verify(accountService).createAccount(createAccount);
    }

    @Test
    public void shouldFailCreateAccountWithNegativeBalance() {
        var balance = BigDecimal.valueOf(-1.1);

        var createAccount = new CreateAccount();
        createAccount.setBalance(balance);

        var response = resource.target("/accounts")
                               .request()
                               .post(entity(createAccount, APPLICATION_JSON));

        assertThat(response.getStatus(), equalTo(422)); // 422 Unprocessable Entity
    }

    @Test
    public void shouldGetAccount() {
        var accountId = "1-1-1";
        var balance = BigDecimal.valueOf(1.1);

        when(accountService.getAccount(eq(accountId)))
        .thenReturn(new AccountOutput(accountId, balance));

        var response = resource.target("/accounts/" + accountId)
                               .request()
                               .get();

        assertThat(response.getStatus(), equalTo(OK.getStatusCode()));

        var result = response.readEntity(AccountOutput.class);

        assertThat(result.getId(), equalTo(accountId));
        assertThat(result.getBalance(), equalTo(balance));

        verify(accountService).getAccount(accountId);
    }

    @Test
    public void shouldReturnNotFound() {
        var accountId = "1-1-1";

        when(accountService.getAccount(eq(accountId)))
        .thenThrow(AccountNotFoundException.class);

        var response = resource.target("/accounts/" + accountId)
                               .request()
                               .get();

        assertThat(response.getStatus(), equalTo(NOT_FOUND.getStatusCode()));
    }

    @Test
    public void shouldTransferBetweenAccounts() {
        var fromId = "1-1-1";
        var fromBalance = BigDecimal.valueOf(1.1);

        var toId = "2-2-2";
        var toBalance = BigDecimal.valueOf(1.1);

        var transfer = new Transfer();
        transfer.setSum(BigDecimal.valueOf(1.1));

        when(accountService.transferResources(eq(fromId), eq(toId), eq(transfer)))
        .thenReturn(new TransferOutput(new AccountOutput(fromId, fromBalance), new AccountOutput(toId, toBalance)));

        var response = resource.target(String.format("/accounts/%s/transfer/%s", fromId, toId))
                               .request()
                               .put(entity(transfer, APPLICATION_JSON));

        assertThat(response.getStatus(), equalTo(OK.getStatusCode()));

        var result = response.readEntity(TransferOutput.class);

        assertThat(result.getFrom().getId(), equalTo(fromId));
        assertThat(result.getFrom().getBalance(), equalTo(fromBalance));

        assertThat(result.getTo().getId(), equalTo(toId));
        assertThat(result.getTo().getBalance(), equalTo(toBalance));
    }

    @Test
    public void shouldNotTransferZeroSum() {
        var fromId = "1-1-1";
        var toId = "2-2-2";

        var transfer = new Transfer();
        transfer.setSum(BigDecimal.valueOf(0));

        var response = resource.target(String.format("/accounts/%s/transfer/%s", fromId, toId))
                               .request()
                               .put(entity(transfer, APPLICATION_JSON));

        assertThat(response.getStatus(), equalTo(422)); // 422 Unprocessable Entity
    }

    @Test
    public void shouldNotTransferNegativeSum() {
        var fromId = "1-1-1";
        var toId = "2-2-2";

        var transfer = new Transfer();
        transfer.setSum(BigDecimal.valueOf(-1.1));

        var response = resource.target(String.format("/accounts/%s/transfer/%s", fromId, toId))
                               .request()
                               .put(entity(transfer, APPLICATION_JSON));

        assertThat(response.getStatus(), equalTo(422)); // 422 Unprocessable Entity
    }

    @Test
    public void shouldNotTransferToSameAccount() {
        var fromId = "1-1-1";

        var transfer = new Transfer();
        transfer.setSum(BigDecimal.valueOf(1.1));

        when(accountService.transferResources(eq(fromId), eq(fromId), eq(transfer)))
        .thenThrow(IllegalArgumentException.class);

        var response = resource.target(String.format("/accounts/%s/transfer/%s", fromId, fromId))
                               .request()
                               .put(entity(transfer, APPLICATION_JSON));

        assertThat(response.getStatus(), equalTo(BAD_REQUEST.getStatusCode()));
    }

    @Test
    public void shouldDeleteAccount() {
        var id = "1-1-1";

        var response = resource.target(String.format("/accounts/%s", id))
                               .request()
                               .delete();

        assertThat(response.getStatus(), equalTo(NO_CONTENT.getStatusCode()));
    }
}
