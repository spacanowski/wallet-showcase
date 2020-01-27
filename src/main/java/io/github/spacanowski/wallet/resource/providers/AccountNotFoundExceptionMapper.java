package io.github.spacanowski.wallet.resource.providers;

import io.github.spacanowski.wallet.exception.AccountNotFoundException;
import io.github.spacanowski.wallet.model.output.ErrorOutput;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class AccountNotFoundExceptionMapper implements ExceptionMapper<AccountNotFoundException> {

    @Override
    public Response toResponse(AccountNotFoundException exception) {
        return Response.status(Status.NOT_FOUND)
                       .entity(new ErrorOutput(exception.getMessage()))
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }
}
