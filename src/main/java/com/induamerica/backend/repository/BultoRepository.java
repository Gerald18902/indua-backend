package com.induamerica.backend.repository;

import com.induamerica.backend.model.Bulto;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BultoRepository extends JpaRepository<Bulto, Long> {
    List<Bulto> findByCargaIdCarga(Long idCarga);

    Bulto findByCodigoBulto(String codigoBulto);

    List<Bulto> findByCargaCodigoCarga(String codigoCarga);

    List<Bulto> findByCarga_IdCarga(Long idCarga);

}
