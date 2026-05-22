package com.BookPoint.pedidos.model;

import lombok.Data;

@Data
public class CarroDTO {
    private Long idCarro;
    private String fechaCreacion;
    private Integer subtotal;
    private Integer total;
}
