package com.induamerica.backend.repository;

import com.induamerica.backend.model.Acta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActaRepository extends JpaRepository<Acta, Long> {
    Optional<Acta> findByCodigoBulto(String codigoBulto);
    boolean existsByCodigoBulto(String codigoBulto);

}
