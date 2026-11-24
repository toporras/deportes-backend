package com.tienda.deportes_backend.controller;

import com.tienda.deportes_backend.model.Usuario;
import com.tienda.deportes_backend.repository.UsuarioRepository;
import com.tienda.deportes_backend.seguridad.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import java.util.Collections;
import java.util.UUID;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {
    private static final String GOOGLE_CLIENT_ID = "363954419431-udb0unt0hpmbh00g4qhj9rtu0cbl1a3b.apps.googleusercontent.com";
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("error", "El correo ya esta registrado"));
        }

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        if (usuario.getRol() == null || usuario.getRol().isEmpty()) {
            usuario.setRol("CLIENTE");
        } else {
            usuario.setRol(usuario.getRol().toUpperCase());
        }

        Usuario guardado = usuarioRepository.save(usuario);
        return ResponseEntity.ok(Map.of("mensaje", "Usuario registrado correctamente", "id", guardado.getId()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        String email = credenciales.get("email");
        String password = credenciales.get("password");

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isPresent() && passwordEncoder.matches(password, usuarioOpt.get().getPassword())) {
            Usuario usuario = usuarioOpt.get();
            String token = jwtService.generateToken(usuario.getEmail());

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("token", token);
            respuesta.put("id", usuario.getId());
            respuesta.put("nombre", usuario.getNombre());
            respuesta.put("email", usuario.getEmail());
            respuesta.put("rol", usuario.getRol());

            return ResponseEntity.ok(respuesta);
        }

        return ResponseEntity.status(401).body(Map.of("error", "Credenciales incorrectas"));
    }
    @PostMapping("/google")
public ResponseEntity<?> loginConGoogle(@RequestBody Map<String, String> body) {
    String credential = body.get("credential");
    if (credential == null || credential.isBlank()) {
        return ResponseEntity.badRequest().body(Map.of("error", "Token de Google no recibido."));
    }

    try {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance()
        )
        .setAudience(Collections.singletonList(GOOGLE_CLIENT_ID))
        .build();

        GoogleIdToken idToken = verifier.verify(credential);
        if (idToken == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Token de Google inválido."));
        }

        GoogleIdToken.Payload payload = idToken.getPayload();
        String email = payload.getEmail();
        String nombre = (String) payload.get("name");

        Usuario usuario = usuarioRepository.findByEmail(email).orElseGet(() -> {
            Usuario nuevo = new Usuario();
            nuevo.setEmail(email);
            nuevo.setNombre(nombre != null ? nombre : email.split("@")[0]);
            nuevo.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
            nuevo.setRol("CLIENTE");
            return usuarioRepository.save(nuevo);
        });

        String token = jwtService.generateToken(usuario.getEmail());

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("token", token);
        respuesta.put("id", usuario.getId());
        respuesta.put("nombre", usuario.getNombre());
        respuesta.put("email", usuario.getEmail());
        respuesta.put("rol", usuario.getRol());

        return ResponseEntity.ok(respuesta);
    } catch (Exception e) {
        return ResponseEntity.status(401)
                .body(Map.of("error", "Error al verificar cuenta de Google: " + e.getMessage()));
    }
}

}