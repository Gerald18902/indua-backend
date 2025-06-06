package com.induamerica.backend.controller;

import com.induamerica.backend.model.Usuario;
import com.induamerica.backend.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // 1. Listar todos los usuarios
    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        return ResponseEntity.ok(usuarioRepository.findAll());
    }

    // 2. Editar usuario (excepto password)
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Integer id, @RequestBody Usuario datosActualizados) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        if (optionalUsuario.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Usuario usuario = optionalUsuario.get();
        usuario.setNombre(datosActualizados.getNombre());
        usuario.setApellido(datosActualizados.getApellido());
        usuario.setUsername(datosActualizados.getUsername());
        usuario.setRol(datosActualizados.getRol());
        usuario.setEstado(datosActualizados.getEstado());

        usuarioRepository.save(usuario);
        return ResponseEntity.ok("Usuario actualizado correctamente");
    }

    // 3. Activar o desactivar usuario
    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstadoUsuario(@PathVariable Integer id, @RequestBody Boolean nuevoEstado) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        if (optionalUsuario.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Usuario usuario = optionalUsuario.get();
        usuario.setEstado(nuevoEstado);
        usuarioRepository.save(usuario);

        return ResponseEntity.ok("Estado actualizado correctamente");
    }
}
