package com.induamerica.backend.repository;

import com.induamerica.backend.model.Bulto;
import com.induamerica.backend.dto.BultoDespachoDTO;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BultoRepository extends JpaRepository<Bulto, Long> {

    List<Bulto> findByCargaIdCarga(Long idCarga);

    Bulto findByCodigoBulto(String codigoBulto);

    List<Bulto> findByCargaCodigoCarga(String codigoCarga);

    List<Bulto> findByCarga_IdCarga(Long idCarga);

    @Query("""
                SELECT new com.induamerica.backend.dto.BultoDespachoDTO(
                    b.codigoBulto, l.codigo, l.nombre,
                    c.codigoCarga, c.fechaCarga,
                    b.estadoDespacho
                )
                FROM Bulto b
                JOIN b.local l
                JOIN b.carga c
                WHERE b.estadoTransporte = com.induamerica.backend.model.Bulto.EstadoTransporte.EN_CAMINO
            """)
    List<BultoDespachoDTO> findBultosEnCamino();

}
