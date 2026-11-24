package com.tienda.deportes_backend.controller;

import com.tienda.deportes_backend.model.Contacto;
import com.tienda.deportes_backend.repository.ContactoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/contactos")
@CrossOrigin(origins = "http://localhost:5173")
public class ContactoController {

    @Autowired
    private ContactoRepository contactoRepository;

    @GetMapping
    public List<Contacto> listarMensajes() {
        return contactoRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> guardarMensaje(@RequestBody Contacto contacto) {
        if (contacto.getNombre() == null || contacto.getNombre().isBlank() ||
            contacto.getEmail() == null || contacto.getEmail().isBlank() ||
            contacto.getMensaje() == null || contacto.getMensaje().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Todos los campos (nombre, correo y mensaje) son obligatorios."));
        }

        Contacto guardado = contactoRepository.save(contacto);
        return ResponseEntity.ok(guardado);
    }
}