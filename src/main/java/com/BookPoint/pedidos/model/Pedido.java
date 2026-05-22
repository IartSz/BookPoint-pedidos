package com.BookPoint.pedidos.model;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Long idUsuario;
    
    @Column(nullable = false)
    private String nombreCliente;

    @Column(nullable = false)
    private String detallePedido;

    @Column(nullable = false)
    private Boolean estadoPedido;

    @Column(nullable = false)
    private LocalDate fechaPedido;


    // @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    // @JsonManagedReference
    // private List<Producto> productos;
}
