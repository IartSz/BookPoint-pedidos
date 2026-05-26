package com.BookPoint.pedidos.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.BookPoint.pedidos.enums.EstadoPedido;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@JsonPropertyOrder({
    "idPedido",
    "mensajeConfirmacion",
    "cliente",
    "fecha",
    "tipoEntrega",
    "direccionEnvio",
    "subtotal",
    "codigoCupon",
    "total",
    "resumenCompra",
    "items"
})

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPedido;
    
    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long idUsuario;

    @Column(nullable = false)
    private Integer total;
    
    @Column(nullable = false)
    @JsonProperty("cliente")
    private String nombreCliente;

    @Column(nullable = false)
    @JsonProperty("fecha")
    private LocalDate fechaPedido;
    
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long idDireccion;
    private String tipoEntrega;
    private String direccionEnvio;

    private String codigoCupon;
    @JsonIgnore
    private Long idCupon;
    private Double subtotal;

    private String mensajeConfirmacion;
    private String resumenCompra;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @JsonProperty("estado")
    @JsonIgnore
    private EstadoPedido estadoPedido;

    @JsonManagedReference
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ItemPedido> items = new ArrayList<>();
}
