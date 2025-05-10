package com.induamerica.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ruta_personzalizada")
@Getter
@Setter
public class RutaPersonalizada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRutaPersonalizada;

    @ManyToOne
    @JoinColumn(name = "id_carga", nullable = false)
    private Carga carga;

    @ManyToOne
    @JoinColumn(name = "id_unidad", nullable = false)
    private UnidadTransporte unidad;

    @Column(columnDefinition = "TEXT")
    private String comentario;
}
