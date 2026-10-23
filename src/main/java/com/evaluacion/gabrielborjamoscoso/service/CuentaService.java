package com.evaluacion.gabrielborjamoscoso.service;

import com.evaluacion.gabrielborjamoscoso.dto.request.CuentaRequestDto;
import com.evaluacion.gabrielborjamoscoso.dto.response.CuentaResponseDto;
import com.evaluacion.gabrielborjamoscoso.dto.response.CuentaResumenDto;
import com.evaluacion.gabrielborjamoscoso.dto.response.PageResponseDto;
import com.evaluacion.gabrielborjamoscoso.entity.Cuenta;

import java.util.List;

public interface CuentaService {

    CuentaResponseDto registrarCuenta(CuentaRequestDto requestDto);

    List<CuentaResponseDto> listarCuentas();

    PageResponseDto<CuentaResponseDto> listarCuentasPaginadas(int pagina, int tamano);

    Cuenta obtenerEntidadPorId(Long id);

    Cuenta obtenerEntidadPorNumero(String numeroCuenta);

    CuentaResumenDto obtenerResumen(Long id);
}
