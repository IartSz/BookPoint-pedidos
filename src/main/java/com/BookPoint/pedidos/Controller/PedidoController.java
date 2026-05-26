package com.BookPoint.pedidos.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.BookPoint.pedidos.model.Pedido;
import com.BookPoint.pedidos.service.PedidoService;

@RestController
@RequestMapping("api/v1/pedidos")
public class PedidoController {
    @Autowired
    private PedidoService pedidoService;


    @PostMapping("/checkout/{idCarro}")
    public ResponseEntity<?> checkout(@PathVariable Long idCarro){
        try{
            Pedido pedido = pedidoService.crearCarro(idCarro);
            if(pedido == null){
                return new ResponseEntity<>("Carro no encontrado o vacio", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(pedido, HttpStatus.OK);
        } catch(RuntimeException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }


    @GetMapping()
    public ResponseEntity<List<Pedido>> getPedidos(){
        try{
            return ResponseEntity.ok(pedidoService.listar());
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }


    @DeleteMapping("{id}")
    public ResponseEntity<?> deletePedido(@PathVariable Long id){
        try{
            pedidoService.eliminar(id);
            return ResponseEntity.ok().build();
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
