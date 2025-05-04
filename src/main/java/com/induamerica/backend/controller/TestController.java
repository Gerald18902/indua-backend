package com.induamerica.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/test")
    public String testConnection() {
        return "Conexión exitosa entre Frontend, Backend y Base de Datos 🚀";
    }
}
