package com.evaluacion.gabrielborjamoscoso.resource;

import com.evaluacion.gabrielborjamoscoso.dto.request.CuentaRequestDto;
import com.evaluacion.gabrielborjamoscoso.dto.response.CuentaResponseDto;
import com.evaluacion.gabrielborjamoscoso.dto.response.CuentaResumenDto;
import com.evaluacion.gabrielborjamoscoso.dto.response.ErrorResponseDto;
import com.evaluacion.gabrielborjamoscoso.service.CuentaService;
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

@Path("/api/cuentas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Cuentas Bancarias", description = "Endpoints para la gestión, registro y consulta de cuentas bancarias")
public class CuentaResource {

    @Inject
    CuentaService cuentaService;

    @POST
    @Operation(
            summary = "Registrar nueva cuenta bancaria (CA-01)",
            description = "Crea y persiste una nueva cuenta bancaria validando número único de 10 dígitos, titular, tipo y saldo inicial no negativo."
    )
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "201",
                    description = "Cuenta registrada exitosamente",
                    content = @Content(schema = @Schema(implementation = CuentaResponseDto.class))
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Datos inválidos o número de cuenta duplicado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    public Response registrarCuenta(@Valid CuentaRequestDto request) {
        CuentaResponseDto creado = cuentaService.registrarCuenta(request);
        return Response.status(Response.Status.CREATED).entity(creado).build();
    }

    @GET
    @Operation(
            summary = "Listar cuentas bancarias (CA-01 / Extra Paginación)",
            description = "Retorna el listado completo de cuentas registradas o un listado paginado si se especifican los parámetros."
    )
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "Lista de cuentas obtenida correctamente"
            )
    })
    public Response listarCuentas(
            @Parameter(description = "Número de página (base 0)") @QueryParam("pagina") Integer pagina,
            @Parameter(description = "Cantidad de elementos por página") @QueryParam("tamano") Integer tamano) {

        if (pagina != null && tamano != null && tamano > 0) {
            return Response.ok(cuentaService.listarCuentasPaginadas(pagina, tamano)).build();
        }
        return Response.ok(cuentaService.listarCuentas()).build();
    }

    @GET
    @Path("/{id}/resumen")
    @Operation(
            summary = "Obtener resumen operativo de cuenta (CA-05)",
            description = "Calcula en tiempo real el saldo actual, total transferido hoy, transferencias del día y cantidad marcadas como sospechosas."
    )
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "Resumen de la cuenta calculado exitosamente",
                    content = @Content(schema = @Schema(implementation = CuentaResumenDto.class))
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Cuenta no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    public Response obtenerResumen(
            @Parameter(description = "ID de la cuenta a consultar", required = true) @PathParam("id") Long id) {
        CuentaResumenDto resumen = cuentaService.obtenerResumen(id);
        return Response.ok(resumen).build();
    }
}
