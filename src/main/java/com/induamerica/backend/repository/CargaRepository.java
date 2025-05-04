package com.induamerica.backend.repository;

import com.induamerica.backend.model.Carga;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CargaRepository extends JpaRepository<Carga, Integer> {
    boolean existsByCodigoCarga(String codigoCarga);
}
