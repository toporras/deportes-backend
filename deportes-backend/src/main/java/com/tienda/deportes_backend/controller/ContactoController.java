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
        try {
            if (contacto.getNombre() == null || contacto.getNombre().isBlank() ||
                contacto.getEmail() == null || contacto.getEmail().isBlank() ||
                contacto.getMensaje() == null || contacto.getMensaje().isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Todos los campos son obligatorios."));
            }

            Contacto guardado = contactoRepository.save(contacto);
            System.out.println("Contacto guardado en MySQL con ID: " + guardado.getId());
            return ResponseEntity.ok(guardado);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error al guardar contacto: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarMensaje(@PathVariable Long id) {
        if (!contactoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        contactoRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("mensaje", "Consulta eliminada"));
    }
}