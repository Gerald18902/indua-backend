package com.induamerica.backend.repository;

import com.induamerica.backend.model.RutaPersonalizada;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RutaPersonalizadaRepository extends JpaRepository<RutaPersonalizada, Long> {
    List<RutaPersonalizada> findAllByCarga_IdCarga(Long idCarga);
    
    Optional<RutaPersonalizada> findByCarga_IdCarga(Long idCarga);

    List<RutaPersonalizada> findByCarga_FechaCarga(LocalDate fecha);

    List<RutaPersonalizada> findByCarga_CodigoCargaContainingIgnoreCase(String codigo);
}