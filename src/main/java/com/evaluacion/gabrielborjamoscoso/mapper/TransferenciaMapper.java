package com.evaluacion.gabrielborjamoscoso.mapper;

import com.evaluacion.gabrielborjamoscoso.dto.response.TransferenciaResponseDto;
import com.evaluacion.gabrielborjamoscoso.entity.Transferencia;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA_CDI)
public interface TransferenciaMapper {

    @Mapping(target = "numeroCuentaOrigen", source = "cuentaOrigen.numeroCuenta")
    @Mapping(target = "titularOrigen", source = "cuentaOrigen.titular")
    @Mapping(target = "numeroCuentaDestino", source = "cuentaDestino.numeroCuenta")
    @Mapping(target = "titularDestino", source = "cuentaDestino.titular")
    TransferenciaResponseDto toDto(Transferencia entity);

    List<TransferenciaResponseDto> toDtoList(List<Transferencia> entities);
}
