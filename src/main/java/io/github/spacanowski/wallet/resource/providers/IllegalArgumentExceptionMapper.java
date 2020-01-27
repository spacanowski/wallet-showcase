package io.github.spacanowski.wallet.resource.providers;

import io.github.spacanowski.wallet.model.output.ErrorOutput;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {

    @Override
    public Response toResponse(IllegalArgumentException exception) {
        return Response.status(Status.BAD_REQUEST)
                       .entity(new ErrorOutput(exception.getMessage()))
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }
}
