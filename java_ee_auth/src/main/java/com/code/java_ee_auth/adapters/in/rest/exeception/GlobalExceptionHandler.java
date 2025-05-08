package com.code.java_ee_auth.adapters.in.rest.exeception;

import com.code.java_ee_auth.domain.dto.response.MessageDTO;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.logging.Logger;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {
    private static final Logger logger = Logger.getLogger(GlobalExceptionHandler.class.getName());

    @Override
    public Response toResponse(Exception exception) {
        logger.severe("Erro capturado no GlobalExceptionHandler: " + exception.getMessage() + " - " + exception);
        exception.printStackTrace();

        if (exception instanceof UserDAOException e) {
            return buildResponse(e.getHttpStatus(), e.getMessage());
        }

        if (exception instanceof UserAlreadyExistsException e) {
            return buildResponse(Response.Status.CONFLICT, e.getMessage());
        }

        if (exception instanceof UserNotFoundException e) {
            return buildResponse(Response.Status.CONFLICT, e.getMessage());
        }

        return buildResponse(Response.Status.INTERNAL_SERVER_ERROR, "Erro interno do servidor!");
    }

    private Response buildResponse(Response.Status status, String message) {
        return Response.status(status)
                .entity(new MessageDTO(message))
                .build();
    }
}
