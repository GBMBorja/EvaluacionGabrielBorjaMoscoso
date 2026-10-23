package com.evaluacion.gabrielborjamoscoso.service;

import com.evaluacion.gabrielborjamoscoso.exception.BusinessException;
import com.evaluacion.gabrielborjamoscoso.repository.TransferenciaRepository;
import com.evaluacion.gabrielborjamoscoso.service.impl.LimiteDiarioServiceImpl;
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
class LimiteDiarioServiceTest {

    @Mock
    TransferenciaRepository transferenciaRepository;

    @InjectMocks
    LimiteDiarioServiceImpl service;

    @Test
    @DisplayName("Debe permitir transferencia si el acumulado diario no supera $10,000")
    void testTransferenciaDentroDelLimite() {
        when(transferenciaRepository.sumTransferredToday(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(new BigDecimal("7000.00"));

        assertDoesNotThrow(() -> service.validarLimiteDiario(1L, new BigDecimal("3000.00")));
    }

    @Test
    @DisplayName("Debe rechazar la transferencia informando monto disponible cuando supera el límite de $10,000")
    void testTransferenciaSuperaLimiteDiario() {
        when(transferenciaRepository.sumTransferredToday(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(new BigDecimal("8500.00"));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                service.validarLimiteDiario(1L, new BigDecimal("2000.00"))
        );

        assertEquals(422, exception.getStatusCode());
        assertTrue(exception.getMessage().contains("Límite diario de transferencias superado"));
        assertTrue(exception.getMessage().contains("Monto restante disponible para transferir hoy: $1500.00"));
    }
}
