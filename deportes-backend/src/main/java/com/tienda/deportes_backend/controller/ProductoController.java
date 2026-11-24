package com.tienda.deportes_backend.controller;

import com.tienda.deportes_backend.model.Categoria;
import com.tienda.deportes_backend.model.Producto;
import com.tienda.deportes_backend.repository.CategoriaRepository;
import com.tienda.deportes_backend.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/productos")
@CrossOrigin(origins = "http://localhost:5173")
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    // 1. LISTAR TODOS LOS PRODUCTOS (Público)
    @GetMapping
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    // 2. OBTENER UN PRODUCTO POR ID (Público)
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerProducto(@PathVariable Long id) {
        return productoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. CREAR PRODUCTO NUEVO (Solo ADMIN)
    @PostMapping
    public ResponseEntity<?> crearProducto(@RequestBody Map<String, Object> datos) {
        try {
            Producto producto = new Producto();
            producto.setNombre(datos.get("nombre").toString());
            producto.setPrecio(Integer.parseInt(datos.get("precio").toString()));
            producto.setStock(Integer.parseInt(datos.get("stock").toString()));

            String imagen = datos.get("imagen") != null && !datos.get("imagen").toString().isEmpty()
                    ? datos.get("imagen").toString()
                    : "/Zapatillasrunning.webp";
            producto.setImagen(imagen);

            int oferta = datos.get("oferta") != null
                    ? Integer.parseInt(datos.get("oferta").toString())
                    : 0;
            producto.setOferta(oferta);

            if (datos.get("categoria") != null) {
                String nombreCat = datos.get("categoria").toString();
                Categoria categoria = categoriaRepository.findByNombre(nombreCat).orElse(null);
                producto.setCategoria(categoria);
            }

            Producto guardado = productoRepository.save(producto);
            return ResponseEntity.ok(guardado);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error al crear el producto: " + e.getMessage()));
        }
    }

    // 4. ACTUALIZAR PRECIO, STOCK O CATEGORÍA (Solo ADMIN)
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarProducto(@PathVariable Long id, @RequestBody Map<String, Object> datos) {
        return productoRepository.findById(id).map(producto -> {
            if (datos.get("nombre") != null) {
                producto.setNombre(datos.get("nombre").toString());
            }
            if (datos.get("precio") != null) {
                producto.setPrecio(Integer.parseInt(datos.get("precio").toString()));
            }
            if (datos.get("stock") != null) {
                producto.setStock(Integer.parseInt(datos.get("stock").toString()));
            }
            if (datos.get("imagen") != null) {
                producto.setImagen(datos.get("imagen").toString());
            }
            if (datos.get("oferta") != null) {
                producto.setOferta(Integer.parseInt(datos.get("oferta").toString()));
            }
            if (datos.get("categoria") != null) {
                String nombreCat = datos.get("categoria").toString();
                Categoria categoria = categoriaRepository.findByNombre(nombreCat).orElse(null);
                producto.setCategoria(categoria);
            }

            Producto actualizado = productoRepository.save(producto);
            return ResponseEntity.ok(actualizado);
        }).orElse(ResponseEntity.notFound().build());
    }

    // 5. ELIMINAR PRODUCTO (Solo ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id) {
        if (!productoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        try {
            productoRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("mensaje", "Producto eliminado correctamente"));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "No se puede eliminar este producto porque ya tiene compras registradas en boletas."));
        }
    }
}