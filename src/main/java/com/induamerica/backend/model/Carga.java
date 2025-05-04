package com.induamerica.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "carga")
@Getter
@Setter
public class Carga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCarga;

    private LocalDate fechaCarga;

    private String codigoCarga;

    private String placaCarreta;

    @Enumerated(EnumType.STRING)
    private DuenoCarreta duenoCarreta;

    @OneToMany(mappedBy = "carga", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Bulto> bultos;

    public enum DuenoCarreta {
        ISL, TERCERO
    }
}
