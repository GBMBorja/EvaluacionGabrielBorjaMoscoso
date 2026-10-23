package com.evaluacion.gabrielborjamoscoso.resource;

import com.evaluacion.gabrielborjamoscoso.dto.request.TransferenciaRequestDto;
import com.evaluacion.gabrielborjamoscoso.dto.response.ErrorResponseDto;
import com.evaluacion.gabrielborjamoscoso.dto.response.TransferenciaResponseDto;
import com.evaluacion.gabrielborjamoscoso.entity.EstadoTransferencia;
import com.evaluacion.gabrielborjamoscoso.service.TransferenciaService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.time.LocalDateTime;

@Path("/api/transferencias")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Transferencias Interbancarias", description = "Endpoints para la ejecución, validación y consulta de transferencias interbancarias")
public class TransferenciaResource {

    @Inject
    TransferenciaService transferenciaService;

    @POST
    @Operation(
            summary = "Ejecutar transferencia entre cuentas (CA-02, CA-03, CA-04)",
            description = "Ejecuta una transferencia atómica entre dos cuentas activas, evaluando saldo suficiente, límite diario ($10,000) y detección de fraude/sospecha ($5,000 o >5 transferencias por hora)."
    )
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "201",
                    description = "Transferencia ejecutada correctamente",
                    content = @Content(schema = @Schema(implementation = TransferenciaResponseDto.class))
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Petición inválida o cuentas origen y destino idénticas",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Cuenta de origen o destino no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @APIResponse(
                    responseCode = "422",
                    description = "Error de negocio: saldo insuficiente, cuenta inactiva o límite diario superado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    public Response ejecutarTransferencia(@Valid TransferenciaRequestDto request) {
        TransferenciaResponseDto respuesta = transferenciaService.ejecutarTransferencia(request);
        return Response.status(Response.Status.CREATED).entity(respuesta).build();
    }

    @GET
    @Operation(
            summary = "Consultar historial de transferencias (CA-05 / Extra Paginación)",
            description = "Permite consultar y filtrar transferencias por cuenta origen, cuenta destino, rango de fechas y estado. Los resultados se retornan ordenados descendentemente por fecha."
    )
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "Historial obtenido exitosamente"
            )
    })
    public Response listarTransferencias(
            @Parameter(description = "Filtrar por número de cuenta origen") @QueryParam("cuentaOrigen") String cuentaOrigen,
            @Parameter(description = "Filtrar por número de cuenta destino") @QueryParam("cuentaDestino") String cuentaDestino,
            @Parameter(description = "Fecha inicial (formato ISO: YYYY-MM-DDTHH:mm:ss)") @QueryParam("fechaInicio") String fechaInicioStr,
            @Parameter(description = "Fecha final (formato ISO: YYYY-MM-DDTHH:mm:ss)") @QueryParam("fechaFin") String fechaFinStr,
            @Parameter(description = "Estado de la transferencia (COMPLETADA, RECHAZADA, REQUIERE_REVISION)") @QueryParam("estado") EstadoTransferencia estado,
            @Parameter(description = "Número de página (base 0)") @QueryParam("pagina") Integer pagina,
            @Parameter(description = "Tamaño de página") @QueryParam("tamano") Integer tamano) {

        LocalDateTime fechaInicio = fechaInicioStr != null && !fechaInicioStr.isBlank()
                ? LocalDateTime.parse(fechaInicioStr) : null;
        LocalDateTime fechaFin = fechaFinStr != null && !fechaFinStr.isBlank()
                ? LocalDateTime.parse(fechaFinStr) : null;

        if (pagina != null && tamano != null && tamano > 0) {
            return Response.ok(transferenciaService.listarTransferenciasPaginadas(
                    cuentaOrigen, cuentaDestino, fechaInicio, fechaFin, estado, pagina, tamano)).build();
        }

        return Response.ok(transferenciaService.listarTransferencias(
                cuentaOrigen, cuentaDestino, fechaInicio, fechaFin, estado)).build();
    }
}
