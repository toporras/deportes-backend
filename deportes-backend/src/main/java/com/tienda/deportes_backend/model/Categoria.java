package com.tienda.deportes_backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "CATEGORIA")
@Data
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;
}