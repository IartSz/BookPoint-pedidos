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
    public ResponseEntity<?> getPedidos(){
        try{
            List<Pedido> pedidos = pedidoService.listar();
            if(pedidos.isEmpty()){
                return new ResponseEntity<>("No hay pedidos registrados", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(pedidos, HttpStatus.OK);
        } catch(RuntimeException e){
            return new ResponseEntity<>("Error al obtener pedidos", HttpStatus.CONFLICT);
        }
    }


    @DeleteMapping("{id}")
    public ResponseEntity<?> deletePedido(@PathVariable Long id){
        try{
            pedidoService.eliminar(id);
            return new ResponseEntity<>("Pedido eliminado correctamente", HttpStatus.OK);
        } catch (RuntimeException e){
            return new ResponseEntity<>("Pedido no encontrado", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
    Pedido buscado = pedidoService.findById(id).orElse(null);
    if (buscado == null) {
        return new ResponseEntity<>("Pedido con id " + id + " no existe", HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(buscado, HttpStatus.OK);
}
}
