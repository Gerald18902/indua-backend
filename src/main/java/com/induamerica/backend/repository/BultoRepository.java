package com.induamerica.backend.repository;

import com.induamerica.backend.model.Bulto;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BultoRepository extends JpaRepository<Bulto, Integer> {
    List<Bulto> findByCargaIdCarga(int idCarga);
}
