package com.induamerica.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "acta")
@Getter
@Setter
public class Acta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idActa;

    private LocalDate fechaIncidencia;

    private String codigoBulto;

    private String numeroActa;

    private String nombreAuxiliar;

    private String nombre;

    @Enumerated(EnumType.STRING)
    private TipoMerma tipoMerma;

    private String estadoMerma;

    private Integer cantidad;

    private String fotoRegistro;

    private String fotoRegularizacion;

    private String responsabilidad;

    private LocalDate fechaRegularizacion;

}
