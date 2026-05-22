package com.BookPoint.pedidos.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.BookPoint.pedidos.model.Pedido;
import com.BookPoint.pedidos.service.PedidoService;

@RestController
@RequestMapping("api/v1/pedidos")
public class PedidoController {
    @Autowired
    private PedidoService pedidoService;

    @PostMapping()
    public Pedido postPedido(@RequestBody Pedido pedido){
        return pedidoService.crearPedido(pedido);
    }

    @GetMapping()
    public List<Pedido> getPedidos(){
        return pedidoService.listar();
    }

    @DeleteMapping("{id}")
    public void deletePedido (@PathVariable Long id){
        pedidoService.eliminar(id);
    }
}
