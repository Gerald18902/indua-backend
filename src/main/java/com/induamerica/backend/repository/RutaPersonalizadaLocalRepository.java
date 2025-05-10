package com.induamerica.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.induamerica.backend.model.RutaPersonalizadaLocal;

public interface RutaPersonalizadaLocalRepository extends JpaRepository<RutaPersonalizadaLocal, Long> {
    List<RutaPersonalizadaLocal> findByRutaPersonalizada_IdRutaPersonalizadaOrderByOrdenAsc(Long idRutaPersonalizada);

    List<RutaPersonalizadaLocal> findByRutaPersonalizada_IdRutaPersonalizada(Long idRutaPersonalizada);
}