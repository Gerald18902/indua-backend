package com.induamerica.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CargaRequest {
    private String fechaCarga;      // formato: yyyy-MM-dd
    private String codigoCarga;
    private String placaCarreta;
    private String duenoCarreta;    // valores esperados: "ISL" o "TERCERO"
}
