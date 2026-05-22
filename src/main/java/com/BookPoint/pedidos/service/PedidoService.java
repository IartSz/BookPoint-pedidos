package com.BookPoint.pedidos.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.BookPoint.pedidos.model.CarroDTO;
import com.BookPoint.pedidos.model.Pedido;
import com.BookPoint.pedidos.model.UsuarioDTO;
import com.BookPoint.pedidos.repository.PedidoRepository;

import jakarta.transaction.Transactional;

@Service
public class PedidoService {
    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private RestTemplate restTemplate;
    
    public Pedido crearPedido(Pedido pedido){
        String urlUsuario="http://localhost:8083/api/usuarios/" + pedido.getIdUsuario();
        UsuarioDTO usuario = restTemplate.getForObject(urlUsuario, UsuarioDTO.class);

        if(usuario != null){
            pedido.setFechaPedido(LocalDate.now());
            if(pedido.getEstadoPedido() == null){
                pedido.setEstadoPedido(true);
            }

            pedido.setNombreCliente(usuario.getNombre() + " " + usuario.getApellido());
            
            return pedidoRepository.save(pedido);
        }
        return null;
    }


    public List<Pedido> listar(){
        return pedidoRepository.findAll();
    }

    public void eliminar(Long id){
        pedidoRepository.deleteById(id);
    }
}
