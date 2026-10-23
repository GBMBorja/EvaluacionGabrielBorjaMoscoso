package com.evaluacion.gabrielborjamoscoso.service;

import com.evaluacion.gabrielborjamoscoso.repository.TransferenciaRepository;
import com.evaluacion.gabrielborjamoscoso.service.impl.DeteccionSospechosaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeteccionSospechosaServiceTest {

    @Mock
    TransferenciaRepository transferenciaRepository;

    @InjectMocks
    DeteccionSospechosaServiceImpl service;

    @Test
    @DisplayName("Debe marcar como sospechosa cuando el monto individual supera $5,000")
    void testSospechosaPorMontoMayorA5000() {
        when(transferenciaRepository.countTransfersInLastHour(eq(1L), any(LocalDateTime.class)))
                .thenReturn(0L);

        DeteccionSospechosaService.ResultadoSospecha resultado =
                service.evaluarSospecha(1L, new BigDecimal("5000.01"));

        assertTrue(resultado.sospechosa());
        assertNotNull(resultado.motivo());
        assertTrue(resultado.motivo().contains("supera el umbral permitido de $5,000.00"));
    }

    @Test
    @DisplayName("Debe marcar como sospechosa cuando la cuenta acumula más de 5 transferencias en la última hora")
    void testSospechosaPorFrecuenciaMayorA5EnUltimaHora() {
        when(transferenciaRepository.countTransfersInLastHour(eq(1L), any(LocalDateTime.class)))
                .thenReturn(5L);

        DeteccionSospechosaService.ResultadoSospecha resultado =
                service.evaluarSospecha(1L, new BigDecimal("100.00"));

        assertTrue(resultado.sospechosa());
        assertNotNull(resultado.motivo());
        assertTrue(resultado.motivo().contains("acumula 5 transferencias en la última hora"));
    }

    @Test
    @DisplayName("No debe marcar como sospechosa una operación normal dentro de los límites")
    void testOperacionNormalNoSospechosa() {
        when(transferenciaRepository.countTransfersInLastHour(eq(1L), any(LocalDateTime.class)))
                .thenReturn(2L);

        DeteccionSospechosaService.ResultadoSospecha resultado =
                service.evaluarSospecha(1L, new BigDecimal("4999.99"));

        assertFalse(resultado.sospechosa());
        assertNull(resultado.motivo());
    }
}
