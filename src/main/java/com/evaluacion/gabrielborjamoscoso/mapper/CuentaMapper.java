package com.evaluacion.gabrielborjamoscoso.mapper;

import com.evaluacion.gabrielborjamoscoso.dto.request.CuentaRequestDto;
import com.evaluacion.gabrielborjamoscoso.dto.response.CuentaResponseDto;
import com.evaluacion.gabrielborjamoscoso.entity.Cuenta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA_CDI)
public interface CuentaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "saldo", source = "saldoInicial")
    Cuenta toEntity(CuentaRequestDto dto);

    CuentaResponseDto toDto(Cuenta entity);

    List<CuentaResponseDto> toDtoList(List<Cuenta> entities);
}
