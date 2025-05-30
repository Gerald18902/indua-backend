package com.induamerica.backend.config;

import com.induamerica.backend.model.Usuario;
import com.induamerica.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class StartupConfig implements CommandLineRunner, WebMvcConfigurer {

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
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRol(Usuario.Rol.administrador);
            admin.setEstado(true);

            usuarioRepository.save(admin);
            System.out.println("✅ Usuario administrador creado: admin / admin123");
        } else {
            System.out.println(" Ya existen usuarios registrados. No se creó ningún usuario por defecto.");
        }

        // Crear carpeta uploads si no existe
        File uploadsFolder = new File("uploads");
        if (!uploadsFolder.exists()) {
            boolean creada = uploadsFolder.mkdirs();
            if (creada) {
                System.out.println("📂 Carpeta 'uploads' creada correctamente.");
            }
        }
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
