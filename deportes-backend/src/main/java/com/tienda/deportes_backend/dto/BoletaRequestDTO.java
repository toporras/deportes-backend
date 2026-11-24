package com.tienda.deportes_backend.dto;

import java.util.List;

public class BoletaRequestDTO {
    private Integer total;
    private List<DetalleBoletaDTO> detalles;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<DetalleBoletaDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleBoletaDTO> detalles) {
        this.detalles = detalles;
    }
}