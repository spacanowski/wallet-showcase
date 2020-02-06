package io.github.spacanowski.wallet;

import static java.lang.String.format;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.assertj.core.api.Assertions.assertThat;

import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.github.spacanowski.wallet.configuration.SimpleWalletConfiguration;
import io.github.spacanowski.wallet.model.input.CreateAccount;
import io.github.spacanowski.wallet.model.input.Transfer;
import io.github.spacanowski.wallet.model.output.AccountOutput;
import io.github.spacanowski.wallet.model.output.TransferOutput;

import java.math.BigDecimal;

import javax.ws.rs.client.Invocation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(DropwizardExtensionsSupport.class)
public class SimpleWalletIntegrationTest {

    public static final DropwizardAppExtension<SimpleWalletConfiguration> SUPPORT =
                    new DropwizardAppExtension<>(
                        SimpleWalletApplication.class,
                        null,
                        ConfigOverride.config("server.applicationConnectors[0].port", "0")
                    );

    @Test
    public void shouldCreateAccount() {
        var balance = BigDecimal.valueOf(1.1);

        var createAccount = new CreateAccount();
        createAccount.setBalance(balance);

        var response = request("/accounts")
                               .post(entity(createAccount, APPLICATION_JSON));

        assertThat(response.getStatus()).isEqualTo(CREATED.getStatusCode());

        var result = response.readEntity(AccountOutput.class);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getBalance()).isEqualTo(balance);
    }

    @Test
    public void shouldCreateAccountWithZeroResources() {
        var balance = BigDecimal.valueOf(0.0);

        var createAccount = new CreateAccount();
        createAccount.setBalance(balance);

        var response = request("/accounts")
                               .post(entity(createAccount, APPLICATION_JSON));

        assertThat(response.getStatus()).isEqualTo(CREATED.getStatusCode());

        var result = response.readEntity(AccountOutput.class);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getBalance()).isEqualTo(balance);
    }

    @Test
    public void shouldFailCreateAccountWithNegativeBalance() {
        var balance = BigDecimal.valueOf(-1.1);

        var createAccount = new CreateAccount();
        createAccount.setBalance(balance);

        var response = request("/accounts")
                               .post(entity(createAccount, APPLICATION_JSON));

        assertThat(response.getStatus()).isEqualTo(422); // 422 Unprocessable Entity
    }

    @Test
    public void shouldGetAccount() {
        var balance = BigDecimal.valueOf(12.1);
        var accountId = createTestAccount(balance);

        var response = request("/accounts/" + accountId)
                               .get();

        assertThat(response.getStatus()).isEqualTo(OK.getStatusCode());

        var result = response.readEntity(AccountOutput.class);

        assertThat(result.getId()).isEqualTo(accountId);
        assertThat(result.getBalance()).isEqualTo(balance);
    }

    @Test
    public void shouldReturnNotFound() {
        var accountId = "1-1-1";

        var response = request("/accounts/" + accountId)
                               .get();

        assertThat(response.getStatus()).isEqualTo(NOT_FOUND.getStatusCode());
    }

    @Test
    public void shouldTransferBetweenAccounts() {
        var transfer = new Transfer();
        var transferSum = BigDecimal.valueOf(1.1);
        transfer.setSum(transferSum);

        var toBalance = BigDecimal.valueOf(1.1);
        var toAccountId = createTestAccount(toBalance);

        var fromBalance = BigDecimal.valueOf(1.1);
        var fromAccountId = createTestAccount(fromBalance);

        var response = request(format("/accounts/%s/transfer/%s", fromAccountId, toAccountId))
                               .put(entity(transfer, APPLICATION_JSON));

        assertThat(response.getStatus()).isEqualTo(OK.getStatusCode());

        var result = response.readEntity(TransferOutput.class);

        assertThat(result.getFrom().getId()).isEqualTo(fromAccountId);
        assertThat(result.getFrom().getBalance()).isEqualTo(fromBalance.subtract(transferSum));

        assertThat(result.getTo().getId()).isEqualTo(toAccountId);
        assertThat(result.getTo().getBalance()).isEqualTo(toBalance.add(transferSum));

        var fromGetResponse = request("/accounts/" + fromAccountId)
                                .get();

        assertThat(fromGetResponse.getStatus()).isEqualTo(OK.getStatusCode());

        var from = fromGetResponse.readEntity(AccountOutput.class);

        assertThat(from.getBalance()).isEqualTo(fromBalance.subtract(transferSum));

        var toGetResponse = request("/accounts/" + toAccountId)
                                .get();

        assertThat(toGetResponse.getStatus()).isEqualTo(OK.getStatusCode());

        var to = toGetResponse.readEntity(AccountOutput.class);

        assertThat(to.getBalance()).isEqualTo(toBalance.add(transferSum));
    }

    @Test
    public void shouldNotTransferIfResourcesAreInsufficient() {
        var transferSum = BigDecimal.valueOf(1.1);
        var transfer = new Transfer();
        transfer.setSum(transferSum);

        var toBalance = BigDecimal.valueOf(1.1);
        var toAccountId = createTestAccount(toBalance);

        var fromBalance = transferSum.subtract(BigDecimal.valueOf(0.1));
        var fromAccountId = createTestAccount(fromBalance);

        var response = request(format("/accounts/%s/transfer/%s", fromAccountId, toAccountId))
                               .put(entity(transfer, APPLICATION_JSON));

        assertThat(response.getStatus()).isEqualTo(BAD_REQUEST.getStatusCode());
    }

    @Test
    public void shouldNotTransferZeroSum() {
        var fromId = "1-1-1";
        var toId = "2-2-2";

        var transfer = new Transfer();
        transfer.setSum(BigDecimal.valueOf(0));

        var response = request(format("/accounts/%s/transfer/%s", fromId, toId))
                               .put(entity(transfer, APPLICATION_JSON));

        assertThat(response.getStatus()).isEqualTo(422); // 422 Unprocessable Entity
    }

    @Test
    public void shouldNotTransferNegativeSum() {
        var fromId = "1-1-1";
        var toId = "2-2-2";

        var transfer = new Transfer();
        transfer.setSum(BigDecimal.valueOf(-1.1));

        var response = request(format("/accounts/%s/transfer/%s", fromId, toId))
                               .put(entity(transfer, APPLICATION_JSON));

        assertThat(response.getStatus()).isEqualTo(422); // 422 Unprocessable Entity
    }

    @Test
    public void shouldNotTransferToSameAccount() {
        var transfer = new Transfer();
        transfer.setSum(BigDecimal.valueOf(1.1));

        var toBalance = BigDecimal.valueOf(1.1);
        var fromId = createTestAccount(toBalance);

        var response = request(format("/accounts/%s/transfer/%s", fromId, fromId))
                               .put(entity(transfer, APPLICATION_JSON));

        assertThat(response.getStatus()).isEqualTo(BAD_REQUEST.getStatusCode());
    }

    @Test
    public void shouldDeleteAccount() {
        var id = createTestAccount(BigDecimal.valueOf(1.1));

        var response = request(format("/accounts/%s", id))
                               .delete();

        assertThat(response.getStatus()).isEqualTo(NO_CONTENT.getStatusCode());

        var getResponse = request("/accounts/" + id)
                               .get();

        assertThat(getResponse.getStatus()).isEqualTo(NOT_FOUND.getStatusCode());
    }

    @Test
    public void shouldGetOperations() {
        var response = request("/accounts/audits")
                               .get();

        assertThat(response.getStatus()).isEqualTo(OK.getStatusCode());
    }

    private Invocation.Builder request(String url) {
        return SUPPORT.client()
                      .target(String.format("http://localhost:%d%s", SUPPORT.getLocalPort(), url))
                      .request();
    }

    private String createTestAccount(BigDecimal balance) {
        var account = new CreateAccount();
        account.setBalance(balance);

        var response = request("/accounts")
                               .post(entity(account, APPLICATION_JSON));

        assertThat(response.getStatus()).isEqualTo(CREATED.getStatusCode());

        return response.readEntity(AccountOutput.class).getId();
    }
}
