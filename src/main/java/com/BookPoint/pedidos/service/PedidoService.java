package com.BookPoint.pedidos.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.BookPoint.pedidos.enums.EstadoPedido;
import com.BookPoint.pedidos.model.CarroDTO;
import com.BookPoint.pedidos.model.CuponDescuentoDTO;
import com.BookPoint.pedidos.model.ItemPedido;
import com.BookPoint.pedidos.model.Pedido;
import com.BookPoint.pedidos.model.ProductoDTO;
import com.BookPoint.pedidos.model.UsuarioDTO;
import com.BookPoint.pedidos.repository.PedidoRepository;

import jakarta.transaction.Transactional;

@Service
public class PedidoService {
    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private RestTemplate restTemplate;

    public Pedido crearPedido(Pedido pedido) {
        String urlUsuario = "http://localhost:8083/api/usuarios/" + pedido.getIdUsuario();
        UsuarioDTO usuario = restTemplate.getForObject(urlUsuario, UsuarioDTO.class);

        if(usuario == null){
            return null;
        }

        pedido.setFechaPedido(LocalDate.now());
        pedido.setEstadoPedido(EstadoPedido.RECIBIDO);
        pedido.setNombreCliente(usuario.getNombre() + " " + usuario.getApellido());

        double totalGeneral = 0.0;
        String detalle = "";

        for(ItemPedido item : pedido.getItems()){
            String urlProducto = "http://localhost:8090/api/v1/productos/" + item.getIdProducto();
            ProductoDTO producto = restTemplate.getForObject(urlProducto, ProductoDTO.class);

            if(producto == null){
                return null;
            }

            item.setNombreProducto(producto.getTitulo());
            item.setPrecioUnitario(producto.getPrecioUnitario().doubleValue());

            double subtotal = producto.getPrecioUnitario() * item.getCantidad().doubleValue();
            item.setSubtotal(subtotal);
            item.setPedido(pedido);
            totalGeneral += subtotal;

            String urlInventario = "http://localhost:8091/api/v1/inventario/descontar/"
                    + item.getIdProducto()
                    + "/" + item.getCantidad();
            restTemplate.put(urlInventario, null);

            detalle += "Libro: " + producto.getTitulo() 
                    + " | Cantidad: " + item.getCantidad()
                    + " | Subtotal: $" + subtotal + "\n";
        }
        pedido.setSubtotal(totalGeneral);
        if(pedido.getCodigoCupon() != null && !pedido.getCodigoCupon().isEmpty()){
            String urlCupon = "http://localhost:8085/api/cupones/" + pedido.getCodigoCupon();
            CuponDescuentoDTO cupon = restTemplate.getForObject(urlCupon, CuponDescuentoDTO.class);
            if(cupon != null && cupon.getActivo() && !cupon.getFechaExpiracion().isBefore(LocalDate.now())){
                Double descuento = totalGeneral * (cupon.getPorcentajeDescuento() / 100.0);
                pedido.setIdCupon(cupon.getIdCupon());
                pedido.setTotal((int) (totalGeneral - descuento));
            } else {
                pedido.setTotal((int) totalGeneral);
            }

        } else {
            pedido.setTotal((int) totalGeneral);
        }
        
        pedido.setDetallePedido(detalle);

        return pedidoRepository.save(pedido);
    }


    public List<Pedido> listar() {
        return pedidoRepository.findAll();
    }

    public void eliminar(Long id) {
        pedidoRepository.deleteById(id);
    }
}
