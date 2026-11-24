package com.tienda.deportes_backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "PRODUCTO")
@Data
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private Integer precio;

    @Column(nullable = false)
    private Integer stock;

    private String imagen;

    private Integer oferta;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;
}