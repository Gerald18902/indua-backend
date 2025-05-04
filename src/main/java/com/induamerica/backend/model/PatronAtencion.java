package com.induamerica.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "patron_atencion")
@Getter
@Setter
public class PatronAtencion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPatronAtencion;

    private String nombre;

    private boolean lunes;
    private boolean martes;
    private boolean miercoles;
    private boolean jueves;
    private boolean viernes;
    private boolean sabado;
    private boolean domingo;
}
