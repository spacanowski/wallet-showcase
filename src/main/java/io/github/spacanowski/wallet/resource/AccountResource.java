package io.github.spacanowski.wallet.resource;

import static java.lang.String.format;

import com.codahale.metrics.annotation.Timed;

import io.github.spacanowski.wallet.model.input.CreateAccount;
import io.github.spacanowski.wallet.model.input.Transfer;
import io.github.spacanowski.wallet.service.AccountService;

import java.net.URI;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__({ @Inject }))
@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
public class AccountResource {

    private final AccountService accountService;

    @Timed
    @GET
    @Path("/audits")
    public Response getAuditData() {
        log.debug("Getting audit data");

        return Response.ok()
                       .entity(accountService.getAuditData())
                       .build();
    }

    @Timed
    @GET
    @Path("/{id}")
    public Response getAccount(@PathParam("id") String id) {
        log.debug("Getting account {}", id);

        return Response.ok()
                       .entity(accountService.getAccount(id))
                       .build();
    }

    @Timed
    @POST
    public Response createAccount(@Valid CreateAccount account) {
        log.debug("Creating account");

        var result = accountService.createAccount(account);

        return Response.created(URI.create(format("/accounts/%s", result.getId())))
                       .entity(result)
                       .build();
    }

    @Timed
    @PUT
    @Path("/{id}/transfer/{to}")
    public Response transferResources(@PathParam("id") String id,
                                      @PathParam("to") String toId,
                                      @Valid Transfer transfer) {
        log.debug("Transfering between {} and {}", id, toId);

        return Response.ok()
                       .entity(accountService.transferResources(id, toId, transfer))
                       .build();
    }

    @Timed
    @DELETE
    @Path("/{id}")
    public Response deleteAccount(@PathParam("id") String id) {
        log.debug("Deleting account {}", id);

        accountService.deleteAccount(id);

        return Response.noContent()
                       .build();
    }
}
