package io.github.spacanowski.wallet.resource;

import com.codahale.metrics.annotation.Timed;

import io.github.spacanowski.wallet.service.AccountService;

import javax.inject.Inject;
import javax.ws.rs.GET;
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
    @Path("/{id}")
    public Response getAccount(@PathParam("id") String id) {
        log.debug("Getting account {}", id);

        return Response.ok()
                       .entity(accountService.getAccount(id))
                       .build();
    }
}
