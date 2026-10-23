package com.evaluacion.gabrielborjamoscoso.exception;

import com.evaluacion.gabrielborjamoscoso.dto.response.ErrorResponseDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOG = Logger.getLogger(GlobalExceptionMapper.class);

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(Throwable exception) {
        String path = uriInfo != null ? uriInfo.getPath() : "";

        if (exception instanceof ResourceNotFoundException || exception instanceof jakarta.ws.rs.NotFoundException) {
            LOG.warnf("Recurso no encontrado: %s", exception.getMessage());
            ErrorResponseDto error = ErrorResponseDto.builder()
                    .timestamp(LocalDateTime.now())
                    .status(Response.Status.NOT_FOUND.getStatusCode())
                    .error("Not Found")
                    .mensaje(exception.getMessage())
                    .path(path)
                    .build();
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }

        if (exception instanceof jakarta.ws.rs.WebApplicationException wae) {
            int statusCode = wae.getResponse().getStatus();
            Response.StatusType statusType = wae.getResponse().getStatusInfo();
            LOG.warnf("Excepción HTTP %d: %s", statusCode, wae.getMessage());
            ErrorResponseDto error = ErrorResponseDto.builder()
                    .timestamp(LocalDateTime.now())
                    .status(statusCode)
                    .error(statusType.getReasonPhrase())
                    .mensaje(wae.getMessage())
                    .path(path)
                    .build();
            return Response.status(statusCode).entity(error).build();
        }

        if (exception instanceof BusinessException be) {
            LOG.warnf("Error de negocio: %s (Status: %d)", be.getMessage(), be.getStatusCode());
            Response.Status status = be.getStatusCode() == 404 ? Response.Status.NOT_FOUND
                    : (be.getStatusCode() == 400 ? Response.Status.BAD_REQUEST : Response.Status.fromStatusCode(422));

            ErrorResponseDto error = ErrorResponseDto.builder()
                    .timestamp(LocalDateTime.now())
                    .status(be.getStatusCode())
                    .error(status != null ? status.getReasonPhrase() : "Unprocessable Entity")
                    .mensaje(be.getMessage())
                    .path(path)
                    .build();
            return Response.status(be.getStatusCode()).entity(error).build();
        }

        if (exception instanceof ConstraintViolationException cve) {
            List<String> detalles = new ArrayList<>();
            for (ConstraintViolation<?> cv : cve.getConstraintViolations()) {
                detalles.add(cv.getPropertyPath() + ": " + cv.getMessage());
            }
            LOG.warnf("Violación de restricciones de validación: %s", detalles);
            ErrorResponseDto error = ErrorResponseDto.builder()
                    .timestamp(LocalDateTime.now())
                    .status(Response.Status.BAD_REQUEST.getStatusCode())
                    .error("Bad Request")
                    .mensaje("Error de validación en la petición")
                    .detalles(detalles)
                    .path(path)
                    .build();
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        if (exception instanceof IllegalArgumentException iae) {
            LOG.warnf("Argumento no válido: %s", iae.getMessage());
            ErrorResponseDto error = ErrorResponseDto.builder()
                    .timestamp(LocalDateTime.now())
                    .status(Response.Status.BAD_REQUEST.getStatusCode())
                    .error("Bad Request")
                    .mensaje(iae.getMessage())
                    .path(path)
                    .build();
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        LOG.errorf(exception, "Error no controlado en la aplicación: %s", exception.getMessage());
        ErrorResponseDto error = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                .error("Internal Server Error")
                .mensaje("Ha ocurrido un error interno en el servidor")
                .path(path)
                .build();
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
    }
}
