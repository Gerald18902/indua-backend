package com.induamerica.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ruta_personalizada_local")
@Getter
@Setter
public class RutaPersonalizadaLocal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // campo artificial si no hay PK compuesta

    @ManyToOne
    @JoinColumn(name = "id_ruta_personalizada")
    private RutaPersonalizada rutaPersonalizada;

    @ManyToOne
    @JoinColumn(name = "id_local")
    private Local local;

    private int orden;
}
