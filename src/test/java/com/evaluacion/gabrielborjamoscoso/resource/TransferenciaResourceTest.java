package com.evaluacion.gabrielborjamoscoso.resource;

import com.evaluacion.gabrielborjamoscoso.dto.request.CuentaRequestDto;
import com.evaluacion.gabrielborjamoscoso.dto.request.TransferenciaRequestDto;
import com.evaluacion.gabrielborjamoscoso.entity.EstadoCuenta;
import com.evaluacion.gabrielborjamoscoso.entity.TipoCuenta;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
class TransferenciaResourceTest {

    @Test
    @DisplayName("Flujo completo: crear cuentas, transferir fondos, validar saldos y consultar historial")
    void testFlujoTransferenciaCompleto() {
        String origenNum = "88" + String.valueOf(System.currentTimeMillis()).substring(5);
        String destinoNum = "99" + String.valueOf(System.currentTimeMillis()).substring(5);

        // 1. Crear Cuenta Origen ($3000.00)
        given()
                .contentType(ContentType.JSON)
                .body(CuentaRequestDto.builder()
                        .numeroCuenta(origenNum)
                        .titular("Origen Test")
                        .tipoCuenta(TipoCuenta.AHORRO)
                        .saldoInicial(new BigDecimal("3000.00"))
                        .estado(EstadoCuenta.ACTIVA)
                        .build())
                .when()
                .post("/api/cuentas")
                .then()
                .statusCode(201);

        // 2. Crear Cuenta Destino ($500.00)
        given()
                .contentType(ContentType.JSON)
                .body(CuentaRequestDto.builder()
                        .numeroCuenta(destinoNum)
                        .titular("Destino Test")
                        .tipoCuenta(TipoCuenta.CORRIENTE)
                        .saldoInicial(new BigDecimal("500.00"))
                        .estado(EstadoCuenta.ACTIVA)
                        .build())
                .when()
                .post("/api/cuentas")
                .then()
                .statusCode(201);

        // 3. Ejecutar Transferencia de $200.00
        given()
                .contentType(ContentType.JSON)
                .body(TransferenciaRequestDto.builder()
                        .numeroCuentaOrigen(origenNum)
                        .numeroCuentaDestino(destinoNum)
                        .monto(new BigDecimal("200.00"))
                        .build())
                .when()
                .post("/api/transferencias")
                .then()
                .statusCode(201)
                .body("numeroCuentaOrigen", equalTo(origenNum))
                .body("numeroCuentaDestino", equalTo(destinoNum))
                .body("monto", equalTo(200.00f))
                .body("estado", equalTo("COMPLETADA"))
                .body("sospechosa", equalTo(false));

        // 4. Consultar Resumen de la Cuenta Origen
        // Obtenemos el ID de la cuenta creada
        int cuentaId = given()
                .when()
                .get("/api/cuentas")
                .then()
                .statusCode(200)
                .extract()
                .path("find { it.numeroCuenta == '" + origenNum + "' }.id");

        given()
                .when()
                .get("/api/cuentas/" + cuentaId + "/resumen")
                .then()
                .statusCode(200)
                .body("saldoActual", equalTo(2800.00f))
                .body("totalTransferidoHoy", equalTo(200.00f))
                .body("cantidadTransferenciasHoy", equalTo(1));

        // 5. Consultar Historial de Transferencias con Filtro
        given()
                .queryParam("cuentaOrigen", origenNum)
                .when()
                .get("/api/transferencias")
                .then()
                .statusCode(200)
                .body("size()", notNullValue());
    }

    @Test
    @DisplayName("Transferencia debe fallar si el saldo es insuficiente (HTTP 422)")
    void testSaldoInsuficiente() {
        String origenNum = "77" + String.valueOf(System.currentTimeMillis()).substring(5);
        String destinoNum = "66" + String.valueOf(System.currentTimeMillis()).substring(5);

        given()
                .contentType(ContentType.JSON)
                .body(CuentaRequestDto.builder()
                        .numeroCuenta(origenNum)
                        .titular("Pobre Test")
                        .tipoCuenta(TipoCuenta.AHORRO)
                        .saldoInicial(new BigDecimal("50.00"))
                        .estado(EstadoCuenta.ACTIVA)
                        .build())
                .when()
                .post("/api/cuentas")
                .then()
                .statusCode(201);

        given()
                .contentType(ContentType.JSON)
                .body(CuentaRequestDto.builder()
                        .numeroCuenta(destinoNum)
                        .titular("Rico Test")
                        .tipoCuenta(TipoCuenta.CORRIENTE)
                        .saldoInicial(new BigDecimal("1000.00"))
                        .estado(EstadoCuenta.ACTIVA)
                        .build())
                .when()
                .post("/api/cuentas")
                .then()
                .statusCode(201);

        given()
                .contentType(ContentType.JSON)
                .body(TransferenciaRequestDto.builder()
                        .numeroCuentaOrigen(origenNum)
                        .numeroCuentaDestino(destinoNum)
                        .monto(new BigDecimal("500.00"))
                        .build())
                .when()
                .post("/api/transferencias")
                .then()
                .statusCode(422)
                .body("mensaje", containsString("Saldo insuficiente"));
    }
}
