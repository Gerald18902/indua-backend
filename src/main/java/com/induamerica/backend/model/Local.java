package com.induamerica.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "local")
@Getter
@Setter
public class Local {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idLocal;

    private String codigo;
    private String nombre;
    private String zona;

    @Column(columnDefinition = "TEXT")
    private String direccion;

    @ManyToOne
    @JoinColumn(name = "id_patron_atencion", nullable = false)
    private PatronAtencion patronAtencion;
}
