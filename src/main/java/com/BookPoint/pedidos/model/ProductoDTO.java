package com.BookPoint.pedidos.model;

import lombok.Data;

@Data
public class ProductoDTO {
    private Long idProducto;
    private String nombreProducto;
    private Integer cantidad;
    private Integer calcularTotal;
}
