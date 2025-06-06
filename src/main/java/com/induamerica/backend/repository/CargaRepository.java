package com.induamerica.backend.repository;

import com.induamerica.backend.model.Carga;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CargaRepository extends JpaRepository<Carga, Long> {
    boolean existsByCodigoCarga(String codigoCarga);

    Optional<Carga> findByCodigoCarga(String codigoCarga);

}
