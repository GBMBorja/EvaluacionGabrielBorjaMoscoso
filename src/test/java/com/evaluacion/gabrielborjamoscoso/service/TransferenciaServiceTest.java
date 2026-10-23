package com.evaluacion.gabrielborjamoscoso.service;

import com.evaluacion.gabrielborjamoscoso.dto.request.TransferenciaRequestDto;
import com.evaluacion.gabrielborjamoscoso.dto.response.TransferenciaResponseDto;
import com.evaluacion.gabrielborjamoscoso.entity.Cuenta;
import com.evaluacion.gabrielborjamoscoso.entity.EstadoCuenta;
import com.evaluacion.gabrielborjamoscoso.entity.EstadoTransferencia;
import com.evaluacion.gabrielborjamoscoso.entity.TipoCuenta;
import com.evaluacion.gabrielborjamoscoso.exception.BusinessException;
import com.evaluacion.gabrielborjamoscoso.exception.ResourceNotFoundException;
import com.evaluacion.gabrielborjamoscoso.mapper.TransferenciaMapper;
import com.evaluacion.gabrielborjamoscoso.repository.CuentaRepository;
import com.evaluacion.gabrielborjamoscoso.repository.TransferenciaRepository;
import com.evaluacion.gabrielborjamoscoso.service.impl.TransferenciaServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferenciaServiceTest {

    @Mock
    CuentaRepository cuentaRepository;

    @Mock
    TransferenciaRepository transferenciaRepository;

    @Mock
    DeteccionSospechosaService deteccionSospechosaService;

    @Mock
    LimiteDiarioService limiteDiarioService;

    @Mock
    TransferenciaMapper transferenciaMapper;

    @InjectMocks
    TransferenciaServiceImpl service;

    @Test
    @DisplayName("Debe lanzar excepción si cuenta de origen y destino son iguales")
    void testMismaCuentaOrigenYDestino() {
        TransferenciaRequestDto request = TransferenciaRequestDto.builder()
                .numeroCuentaOrigen("1234567890")
                .numeroCuentaDestino("1234567890")
                .monto(new BigDecimal("100.00"))
                .build();

        BusinessException exception = assertThrows(BusinessException.class, () ->
                service.ejecutarTransferencia(request)
        );

        assertTrue(exception.getMessage().contains("no puede ser igual"));
    }

    @Test
    @DisplayName("Debe lanzar excepción si la cuenta origen no tiene saldo suficiente")
    void testSaldoInsuficiente() {
        Cuenta origen = Cuenta.builder()
                .id(1L)
                .numeroCuenta("1111111111")
                .titular("Juan Perez")
                .saldo(new BigDecimal("50.00"))
                .tipoCuenta(TipoCuenta.AHORRO)
                .estado(EstadoCuenta.ACTIVA)
                .build();

        Cuenta destino = Cuenta.builder()
                .id(2L)
                .numeroCuenta("2222222222")
                .titular("Ana Gómez")
                .saldo(new BigDecimal("200.00"))
                .tipoCuenta(TipoCuenta.CORRIENTE)
                .estado(EstadoCuenta.ACTIVA)
                .build();

        when(cuentaRepository.findByNumeroCuenta("1111111111")).thenReturn(Optional.of(origen));
        when(cuentaRepository.findByNumeroCuenta("2222222222")).thenReturn(Optional.of(destino));

        TransferenciaRequestDto request = TransferenciaRequestDto.builder()
                .numeroCuentaOrigen("1111111111")
                .numeroCuentaDestino("2222222222")
                .monto(new BigDecimal("150.00"))
                .build();

        BusinessException exception = assertThrows(BusinessException.class, () ->
                service.ejecutarTransferencia(request)
        );

        assertTrue(exception.getMessage().contains("Saldo insuficiente"));
    }

    @Test
    @DisplayName("Debe ejecutar transferencia exitosa actualizando saldos")
    void testTransferenciaExitosa() {
        Cuenta origen = Cuenta.builder()
                .id(1L)
                .numeroCuenta("1111111111")
                .titular("Juan Perez")
                .saldo(new BigDecimal("500.00"))
                .tipoCuenta(TipoCuenta.AHORRO)
                .estado(EstadoCuenta.ACTIVA)
                .build();

        Cuenta destino = Cuenta.builder()
                .id(2L)
                .numeroCuenta("2222222222")
                .titular("Ana Gómez")
                .saldo(new BigDecimal("200.00"))
                .tipoCuenta(TipoCuenta.CORRIENTE)
                .estado(EstadoCuenta.ACTIVA)
                .build();

        when(cuentaRepository.findByNumeroCuenta("1111111111")).thenReturn(Optional.of(origen));
        when(cuentaRepository.findByNumeroCuenta("2222222222")).thenReturn(Optional.of(destino));
        when(deteccionSospechosaService.evaluarSospecha(eq(1L), any(BigDecimal.class)))
                .thenReturn(new DeteccionSospechosaService.ResultadoSospecha(false, null));

        TransferenciaResponseDto responseEsperada = TransferenciaResponseDto.builder()
                .id(100L)
                .numeroCuentaOrigen("1111111111")
                .numeroCuentaDestino("2222222222")
                .monto(new BigDecimal("100.00"))
                .estado(EstadoTransferencia.COMPLETADA)
                .sospechosa(false)
                .fecha(LocalDateTime.now())
                .build();

        when(transferenciaMapper.toDto(any())).thenReturn(responseEsperada);

        TransferenciaRequestDto request = TransferenciaRequestDto.builder()
                .numeroCuentaOrigen("1111111111")
                .numeroCuentaDestino("2222222222")
                .monto(new BigDecimal("100.00"))
                .build();

        TransferenciaResponseDto result = service.ejecutarTransferencia(request);

        assertNotNull(result);
        assertEquals(new BigDecimal("400.00"), origen.getSaldo());
        assertEquals(new BigDecimal("300.00"), destino.getSaldo());
        verify(transferenciaRepository, times(1)).persist(any(com.evaluacion.gabrielborjamoscoso.entity.Transferencia.class));
    }
}
