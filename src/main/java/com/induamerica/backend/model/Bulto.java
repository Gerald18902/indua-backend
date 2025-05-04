package com.induamerica.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "bulto")
@Getter
@Setter
public class Bulto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idBulto;

    private String codigoBulto;

    @ManyToOne
    @JoinColumn(name = "id_local", nullable = false)
    private Local local;

    @ManyToOne
    @JoinColumn(name = "id_carga", nullable = false)
    @JsonBackReference
    private Carga carga;

    @Enumerated(EnumType.STRING)
    private EstadoRecepcion estadoRecepcion;

    @Enumerated(EnumType.STRING)
    private EstadoTransporte estadoTransporte;

    private LocalDate fechaTransporte;

    @Enumerated(EnumType.STRING)
    private EstadoDespacho estadoDespacho;

    private LocalDate fechaDespacho;

    public enum EstadoRecepcion {
        EN_BUEN_ESTADO, DETERIORADO, FALTANTE
    }

    public enum EstadoTransporte {
        EN_ALMACEN, EN_CAMINO
    }

    public enum EstadoDespacho {
        ENTREGADO_EN_BUEN_ESTADO, FALTANTE, ENTREGADO_CON_IRREGULARIDAD
    }
}
