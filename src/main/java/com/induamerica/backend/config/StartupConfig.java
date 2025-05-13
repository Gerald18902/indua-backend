package com.induamerica.backend.config;

import com.induamerica.backend.model.Usuario;
import com.induamerica.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class StartupConfig implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            Usuario admin = new Usuario();
            admin.setNombre("Admin");
            admin.setApellido("General");
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123")); // Contraseña conocida
            admin.setRol(Usuario.Rol.administrador);
            admin.setEstado(true);

            usuarioRepository.save(admin);
            System.out.println("✅ Usuario administrador creado: admin / admin123");
        } else {
            System.out.println("ℹ️ Ya existen usuarios registrados. No se creó ningún usuario por defecto.");
        }
    }
}
