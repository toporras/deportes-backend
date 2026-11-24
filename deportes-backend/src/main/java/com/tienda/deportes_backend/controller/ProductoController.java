package com.tienda.deportes_backend.controller;

import com.tienda.deportes_backend.model.Producto;
import com.tienda.deportes_backend.repository.ProductoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*") // Habilita CORS para React
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    // GET /api/productos - Obtener todos los productos
    @GetMapping
    public List<Producto> obtenerTodos() {
        return productoRepository.findAllByOrderByIdDesc();
    }

    // POST /api/productos - Guardar nuevo producto en MySQL
    @PostMapping
    public ResponseEntity<Producto> guardarProducto(@RequestBody Producto producto) {
        if (producto.getImagen() == null || producto.getImagen().isEmpty()) {
            producto.setImagen("/Zapatillasrunning.webp");
        }
        if (producto.getOferta() == null) {
            producto.setOferta(0);
        }
        Producto nuevoProducto = productoRepository.save(producto);
        return new ResponseEntity<>(nuevoProducto, HttpStatus.CREATED);
    }
}