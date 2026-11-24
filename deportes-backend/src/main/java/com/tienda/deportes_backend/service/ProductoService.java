package com.tienda.deportes_backend.service;

import com.tienda.deportes_backend.model.Categoria;
import com.tienda.deportes_backend.model.Producto;
import com.tienda.deportes_backend.repository.CategoriaRepository;
import com.tienda.deportes_backend.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    public Optional<Producto> buscarPorId(Long id) {
        return productoRepository.findById(id);
    }

    public Producto guardarProducto(Producto producto, String nombreCategoria) {
        if (nombreCategoria != null && !nombreCategoria.isEmpty()) {
            Categoria cat = categoriaRepository.findByNombre(nombreCategoria)
                    .orElseGet(() -> {
                        Categoria nueva = new Categoria();
                        nueva.setNombre(nombreCategoria);
                        return categoriaRepository.save(nueva);
                    });
            producto.setCategoria(cat);
        }
        return productoRepository.save(producto);
    }

    public Producto actualizarProducto(Long id, Producto datosActualizados) {
        Producto existente = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        existente.setNombre(datosActualizados.getNombre());
        existente.setPrecio(datosActualizados.getPrecio());
        existente.setStock(datosActualizados.getStock());
        existente.setImagen(datosActualizados.getImagen());
        existente.setOferta(datosActualizados.getOferta());
        
        return productoRepository.save(existente);
    }

    public void eliminarProducto(Long id) {
        productoRepository.deleteById(id);
    }
}