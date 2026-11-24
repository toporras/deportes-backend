package com.tienda.deportes_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.tienda.deportes_backend.dto.BoletaRequestDTO;
import com.tienda.deportes_backend.dto.DetalleBoletaDTO;
import com.tienda.deportes_backend.model.Boleta;
import com.tienda.deportes_backend.model.DetalleBoleta;
import com.tienda.deportes_backend.model.Producto;
import com.tienda.deportes_backend.model.Usuario;
import com.tienda.deportes_backend.repository.BoletaRepository;
import com.tienda.deportes_backend.repository.DetalleBoletaRepository;
import com.tienda.deportes_backend.repository.ProductoRepository;
import com.tienda.deportes_backend.repository.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/boletas")
@CrossOrigin(origins = "http://localhost:5173")
public class BoletaController {

    private final BoletaRepository boletaRepository;

    @Autowired
    public BoletaController(BoletaRepository boletaRepository) {
        this.boletaRepository = boletaRepository;
    }

    @Autowired
    private DetalleBoletaRepository detalleBoletaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public List<Boleta> listarBoletas() {
        return boletaRepository.findAll();
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> crearBoleta(@RequestBody BoletaRequestDTO request, Authentication authentication) {
        try {
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Token inválido o sesión expirada"));
            }

            Usuario usuario = usuarioRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + authentication.getName()));

            if (request.getDetalles() == null || request.getDetalles().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "El carrito está vacío"));
            }

            Boleta boleta = new Boleta();
            boleta.setUsuario(usuario);
            boleta.setFecha(LocalDateTime.now());
            boleta.setTotal(request.getTotal());
            Boleta boletaGuardada = boletaRepository.save(boleta);

            for (DetalleBoletaDTO item : request.getDetalles()) {
                Producto producto = productoRepository.findById(item.getProductoId())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado ID: " + item.getProductoId()));

                if (producto.getStock() < item.getCantidad()) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Stock insuficiente para: " + producto.getNombre()));
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
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}