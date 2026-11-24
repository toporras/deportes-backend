package com.tienda.deportes_backend.repository;

import com.tienda.deportes_backend.model.Contacto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactoRepository extends JpaRepository<Contacto, Long> {
}