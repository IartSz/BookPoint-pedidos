package com.BookPoint.pedidos.model;
import java.util.List;
import lombok.Data;

@Data
public class CarroDTO {
    private Long idCarro;
    private Long idUsuario;
    private String nombreUsuario;
    private String fechaCreacion;
    private Double subtotal;
    private Double total;

    private String codigoCupon;
    private String tipoEntrega;
    private Long idDireccion;
    private List<ItemCarroDTO> items;
}
