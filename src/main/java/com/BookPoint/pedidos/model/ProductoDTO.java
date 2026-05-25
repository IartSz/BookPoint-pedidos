package com.BookPoint.pedidos.model;

import lombok.Data;

@Data
public class ProductoDTO {
    private Long idProducto;
    private Long idInventario;
    private String titulo;
    private String autor;
    private String editorial;
    private Integer precioUnitario;
    private String nombreProducto;
    private Integer cantidad;
    private Integer calcularTotal;
}
