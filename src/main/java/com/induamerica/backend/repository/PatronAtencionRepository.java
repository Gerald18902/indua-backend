package com.induamerica.backend.repository;

import com.induamerica.backend.model.PatronAtencion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatronAtencionRepository extends JpaRepository<PatronAtencion, Integer> {
}
