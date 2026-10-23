package com.evaluacion.gabrielborjamoscoso.repository;

import com.evaluacion.gabrielborjamoscoso.entity.Cuenta;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class CuentaRepository implements PanacheRepository<Cuenta> {

    public Optional<Cuenta> findByNumeroCuenta(String numeroCuenta) {
        return find("numeroCuenta", numeroCuenta).firstResultOptional();
    }

    public boolean existsByNumeroCuenta(String numeroCuenta) {
        return count("numeroCuenta", numeroCuenta) > 0;
    }
}
