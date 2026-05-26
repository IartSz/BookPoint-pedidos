package com.BookPoint.pedidos.model;

import lombok.Data;

@Data
public class ItemCarroDTO {
    private Long idProducto;
    private String nombreProducto;
    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;
}
