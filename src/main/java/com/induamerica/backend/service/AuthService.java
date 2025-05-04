package com.induamerica.backend.service;

import com.induamerica.backend.dto.LoginRequest;
import com.induamerica.backend.dto.LoginResponse;
import com.induamerica.backend.model.Usuario;
import com.induamerica.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!usuario.getEstado()) {
            throw new RuntimeException("Usuario desactivado");
        }

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        return new LoginResponse(
                "Inicio de sesión exitoso",
                null, // si luego generas JWT
                usuario.getRol().name(),
                usuario.getNombre());
    }

}
