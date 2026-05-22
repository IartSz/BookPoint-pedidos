package com.BookPoint.pedidos.model;

import lombok.Data;

@Data
public class UsuarioDTO {
    private Long idUsuario;
    private String nombre;
    private String apellido;
    private String emailCliente;
}
