package com.induamerica.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String message;
    private String token; // si luego usas JWT
    private String rol;
    private String nombre;
}
