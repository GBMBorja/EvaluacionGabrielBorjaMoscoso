package com.evaluacion.gabrielborjamoscoso.service;

import com.evaluacion.gabrielborjamoscoso.dto.request.TransferenciaRequestDto;
import com.evaluacion.gabrielborjamoscoso.dto.response.PageResponseDto;
import com.evaluacion.gabrielborjamoscoso.dto.response.TransferenciaResponseDto;
import com.evaluacion.gabrielborjamoscoso.entity.EstadoTransferencia;

import java.time.LocalDateTime;
import java.util.List;

public interface TransferenciaService {

    TransferenciaResponseDto ejecutarTransferencia(TransferenciaRequestDto requestDto);

    List<TransferenciaResponseDto> listarTransferencias(
            String cuentaOrigen,
            String cuentaDestino,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            EstadoTransferencia estado);

    PageResponseDto<TransferenciaResponseDto> listarTransferenciasPaginadas(
            String cuentaOrigen,
            String cuentaDestino,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            EstadoTransferencia estado,
            int pagina,
            int tamano);
}
