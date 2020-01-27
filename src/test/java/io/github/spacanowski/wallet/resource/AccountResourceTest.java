package io.github.spacanowski.wallet.resource;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.CREATED;
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
import io.github.spacanowski.wallet.model.input.CreateAccount;
import io.github.spacanowski.wallet.model.output.AccountOutput;
import io.github.spacanowski.wallet.service.AccountService;

import java.math.BigDecimal;

import javax.ws.rs.client.Entity;

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
                               .post(Entity.entity(createAccount, APPLICATION_JSON));

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
                               .post(Entity.entity(createAccount, APPLICATION_JSON));

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
}
