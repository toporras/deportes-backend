package com.tienda.deportes_backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class CompraRequestDTO {
    private Long usuarioId;
    private List<ItemCarritoDTO> carrito;
}