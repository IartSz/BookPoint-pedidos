package com.BookPoint.pedidos.model;

import java.time.LocalDate;

import lombok.Data;

@Data
public class InventarioDTO {
    private Long idInventario;
    private Long idProducto;
    private Long idBodega;
    private String tituloProducto;
    private Integer stockDisponible;
    private LocalDate fechaActualizacion;
}
