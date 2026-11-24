package com.tienda.deportes_backend.dto;

import lombok.Data;

@Data
public class ItemCarritoDTO {
    private Long id;
    private Integer cantidad;
    private Integer precio;
}