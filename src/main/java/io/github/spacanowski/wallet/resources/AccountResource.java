package io.github.spacanowski.wallet.resources;

import com.codahale.metrics.annotation.Timed;

import io.github.spacanowski.wallet.model.Account;

import java.util.Collections;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("accounts")
@Produces(MediaType.APPLICATION_JSON)
public class AccountResource {

    @GET
    @Timed
    public List<Account> getAccounts() {
        return Collections.emptyList();
    }
}
