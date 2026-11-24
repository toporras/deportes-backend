package com.tienda.deportes_backend.controller;

import com.tienda.deportes_backend.model.Producto;
import com.tienda.deportes_backend.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public List<Producto> listar() {
        return productoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id) {
        return productoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Producto> crear(@RequestBody Map<String, Object> payload) {
        Producto p = new Producto();
        p.setNombre((String) payload.get("nombre"));
        p.setPrecio(Integer.parseInt(payload.get("precio").toString()));
        p.setStock(Integer.parseInt(payload.get("stock").toString()));
        p.setImagen((String) payload.get("imagen"));
        p.setOferta(payload.get("oferta") != null ? Integer.parseInt(payload.get("oferta").toString()) : 0);

        String categoria = (String) payload.get("categoria");
        Producto guardado = productoService.guardarProducto(p, categoria);
        return ResponseEntity.ok(guardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id, @RequestBody Producto producto) {
        return ResponseEntity.ok(productoService.actualizarProducto(id, producto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.ok(Map.of("mensaje", "Producto eliminado"));
    }
}