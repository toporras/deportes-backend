package com.tienda.deportes_backend.repository;

import com.tienda.deportes_backend.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository // <-- Importante: le indica a Spring que cree el Bean de datos
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findAllByOrderByIdDesc();
}