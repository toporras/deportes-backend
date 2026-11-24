package com.tienda.deportes_backend.service;

import com.tienda.deportes_backend.dto.BoletaRequestDTO;
import com.tienda.deportes_backend.dto.CompraRequestDTO;
import com.tienda.deportes_backend.dto.DetalleBoletaDTO;
import com.tienda.deportes_backend.dto.ItemCarritoDTO;
import com.tienda.deportes_backend.model.Boleta;
import com.tienda.deportes_backend.model.DetalleBoleta;
import com.tienda.deportes_backend.model.Producto;
import com.tienda.deportes_backend.model.Usuario;
import com.tienda.deportes_backend.repository.BoletaRepository;
import com.tienda.deportes_backend.repository.ProductoRepository;
import com.tienda.deportes_backend.repository.UsuarioRepository;
import com.tienda.deportes_backend.repository.DetalleBoletaRepository;


import io.swagger.v3.oas.annotations.parameters.RequestBody;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BoletaService {

    @Autowired
    private BoletaRepository boletaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private DetalleBoletaRepository detalleBoletaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Boleta> listarTodas() {
        return boletaRepository.findAll();
    }

    public Boleta buscarPorId(Long id) {
        return boletaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Boleta no encontrada"));
    }

    @PostMapping
@Transactional
public ResponseEntity<?> crearBoleta(@RequestBody BoletaRequestDTO request, Authentication authentication) {
    Usuario usuario = usuarioRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    Boleta boleta = new Boleta();
    boleta.setUsuario(usuario);
    boleta.setFecha(LocalDateTime.now());
    boleta.setTotal(request.getTotal());
    Boleta boletaGuardada = boletaRepository.save(boleta);

    for (DetalleBoletaDTO item : request.getDetalles()) {
        Producto producto = productoRepository.findById(item.getProductoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (producto.getStock() < item.getCantidad()) {
            throw new RuntimeException("Stock insuficiente para: " + producto.getNombre());
        }

        producto.setStock(producto.getStock() - item.getCantidad());
        productoRepository.save(producto);

        DetalleBoleta detalle = new DetalleBoleta();
        detalle.setBoleta(boletaGuardada);
        detalle.setProducto(producto);
        detalle.setCantidad(item.getCantidad());
        detalle.setPrecioUnitario(item.getPrecioUnitario());
        detalleBoletaRepository.save(detalle);
    }

    return ResponseEntity.ok(boletaGuardada);
}

    public Boleta procesarCompra(CompraRequestDTO request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'procesarCompra'");
    }
}