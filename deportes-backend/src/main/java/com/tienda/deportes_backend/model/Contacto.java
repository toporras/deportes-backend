package com.tienda.deportes_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "contacto")
@Data
public class Contacto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    private LocalDateTime fecha;

    @PrePersist
    public void asignarFecha() {
        if (this.fecha == null) {
            this.fecha = LocalDateTime.now();
        }
    }
}