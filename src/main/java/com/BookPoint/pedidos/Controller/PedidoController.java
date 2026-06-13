package com.BookPoint.pedidos.Controller;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;

import com.BookPoint.pedidos.model.Pedido;
import com.BookPoint.pedidos.service.PedidoService;

@RestController
@RequestMapping("/api/v1/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @PostMapping("/checkout/{idCarro}")
    public ResponseEntity<EntityModel<Pedido>> checkout(@PathVariable Long idCarro) {
        try {
            Pedido pedido = pedidoService.crearCarro(idCarro);
            if (pedido == null) {
                return ResponseEntity.notFound().build();
            }
            EntityModel<Pedido> pedidoModel = EntityModel.of(pedido,
                    linkTo(methodOn(PedidoController.class).findById(pedido.getIdPedido())).withSelfRel(),
                    linkTo(methodOn(PedidoController.class).getPedidos()).withRel("pedidos"));
            return ResponseEntity.status(HttpStatus.CREATED).body(pedidoModel);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping
    public CollectionModel<EntityModel<Pedido>> getPedidos() {
        List<Pedido> pedidos = pedidoService.listar();
        List<EntityModel<Pedido>> pedidoModels = pedidos.stream()
                .map(pedido -> EntityModel.of(pedido,
                        linkTo(methodOn(PedidoController.class).findById(pedido.getIdPedido())).withSelfRel()))
                .toList();
        return CollectionModel.of(pedidoModels,
                linkTo(methodOn(PedidoController.class).getPedidos()).withSelfRel());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Pedido>> findById(@PathVariable Long id) {
        return pedidoService.findById(id)
                .map(pedido -> ResponseEntity.ok(EntityModel.of(pedido,
                        linkTo(methodOn(PedidoController.class).findById(id)).withSelfRel(),
                        linkTo(methodOn(PedidoController.class).getPedidos()).withRel("pedidos"))))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePedido(@PathVariable Long id) {
        pedidoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

