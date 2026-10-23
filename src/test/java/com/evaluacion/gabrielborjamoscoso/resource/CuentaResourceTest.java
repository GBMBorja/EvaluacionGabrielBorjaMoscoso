package com.evaluacion.gabrielborjamoscoso.resource;

import com.evaluacion.gabrielborjamoscoso.dto.request.CuentaRequestDto;
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
class CuentaResourceTest {

    @Test
    @DisplayName("POST /api/cuentas debe crear una cuenta bancaria con HTTP 201")
    void testCrearCuentaExitoso() {
        String numAleatorio = String.valueOf(System.currentTimeMillis()).substring(3);
        CuentaRequestDto dto = CuentaRequestDto.builder()
                .numeroCuenta(numAleatorio)
                .titular("Cliente Prueba")
                .tipoCuenta(TipoCuenta.AHORRO)
                .saldoInicial(new BigDecimal("1000.00"))
                .estado(EstadoCuenta.ACTIVA)
                .build();

        given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when()
                .post("/api/cuentas")
                .then()
                .statusCode(201)
                .body("numeroCuenta", equalTo(numAleatorio))
                .body("titular", equalTo("Cliente Prueba"))
                .body("saldo", equalTo(1000.00f));
    }

    @Test
    @DisplayName("GET /api/cuentas debe retornar lista con HTTP 200")
    void testListarCuentas() {
        given()
                .when()
                .get("/api/cuentas")
                .then()
                .statusCode(200);
    }
}
