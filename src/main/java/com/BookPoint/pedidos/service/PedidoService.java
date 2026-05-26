package com.BookPoint.pedidos.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.BookPoint.pedidos.enums.EstadoPedido;
import com.BookPoint.pedidos.model.CarroDTO;
import com.BookPoint.pedidos.model.CuponDescuentoDTO;
import com.BookPoint.pedidos.model.Direccion;
import com.BookPoint.pedidos.model.ItemCarroDTO;
import com.BookPoint.pedidos.model.ItemPedido;
import com.BookPoint.pedidos.model.Pedido;
import com.BookPoint.pedidos.model.ProductoDTO;
import com.BookPoint.pedidos.model.UsuarioDTO;
import com.BookPoint.pedidos.repository.DireccionRepository;
import com.BookPoint.pedidos.repository.PedidoRepository;

import jakarta.transaction.Transactional;

@Service
public class PedidoService {
    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DireccionRepository direccionRepository;

    public Pedido crearCarro(Long idCarro) {
        CarroDTO carro = obtenerCarro(idCarro);
        Pedido pedido = construirPedidoBase(carro);
        cargaryDescontar(pedido, carro);
        aplicarCupon(pedido);
        cargarDireccion(pedido);
        pedido.setMensajeConfirmacion(
            "Gracias por tu compra, " + pedido.getNombreCliente()
            + ". Tu pedido fue recibido correctamente."
        );

        Pedido pedidoGuardado = pedidoRepository.save(pedido);
        pedidoGuardado.setResumenCompra(
            "Pedido #" + pedidoGuardado.getIdPedido() +
            "\nCliente: " + pedidoGuardado.getNombreCliente() + 
            "\nEntrega: " + pedidoGuardado.getDireccionEnvio() + 
            "\nSubtotal: $" + pedidoGuardado.getSubtotal() +
            "\nCupón: " + pedidoGuardado.getCodigoCupon() +
            "\nTotal: $" + pedidoGuardado.getTotal()
        );

        return pedidoRepository.save(pedidoGuardado);
    }

    private CarroDTO obtenerCarro(Long idCarro) {
        String urlCarro = "http://localhost:8082/api/v1/carrito/" + idCarro;
        return restTemplate.getForObject(urlCarro, CarroDTO.class);
    }

    private Pedido construirPedidoBase(CarroDTO carro) {

        String urlUsuario = "http://localhost:8083/api/v1/usuarios/" + carro.getIdUsuario();
        UsuarioDTO usuario = restTemplate.getForObject(urlUsuario, UsuarioDTO.class);

        Pedido pedido = new Pedido();

        pedido.setIdUsuario(carro.getIdUsuario());
        pedido.setNombreCliente(usuario.getNombre() + " " + usuario.getApellido());
        pedido.setFechaPedido(LocalDate.now());
        pedido.setEstadoPedido(EstadoPedido.RECIBIDO);

        pedido.setCodigoCupon(carro.getCodigoCupon());
        pedido.setTipoEntrega(carro.getTipoEntrega());
        pedido.setIdDireccion(carro.getIdDireccion());

        return pedido;
    }

    private void cargaryDescontar(Pedido pedido, CarroDTO carro) {

        List<ItemPedido> itemsPedido = new ArrayList<>();
        double totalGeneral = 0.0;

        for (ItemCarroDTO itemCarro : carro.getItems()) {

            ItemPedido item = new ItemPedido();

            item.setIdProducto(itemCarro.getIdProducto());
            item.setNombreProducto(itemCarro.getNombreProducto());
            item.setCantidad(itemCarro.getCantidad());
            item.setPrecioUnitario(itemCarro.getPrecioUnitario());
            item.setSubtotal(itemCarro.getSubtotal());
            item.setPedido(pedido);

            itemsPedido.add(item);
            totalGeneral += itemCarro.getSubtotal();

            descontarStock(itemCarro.getIdProducto(), itemCarro.getCantidad());
        }

        pedido.setItems(itemsPedido);
        pedido.setSubtotal(totalGeneral);
    }

    private void descontarStock(Long idProducto, Integer cantidad) {

        String urlInventario = "http://localhost:8091/api/v1/inventario/descontar/"
                + idProducto + "/" + cantidad;

        try {
            restTemplate.put(urlInventario, null);
        } catch (HttpClientErrorException.Conflict e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Stock insuficiente o producto no encontrado"
            );
        }
    }

    private void aplicarCupon(Pedido pedido) {

        double totalGeneral = pedido.getSubtotal();

        if (pedido.getCodigoCupon() != null && !pedido.getCodigoCupon().isEmpty()) {

            String urlCupon = "http://localhost:8085/api/v1/cupones/" + pedido.getCodigoCupon();
            CuponDescuentoDTO cupon = restTemplate.getForObject(urlCupon, CuponDescuentoDTO.class);

            if (cupon != null && cupon.getActivo()
                    && !cupon.getFechaExpiracion().isBefore(LocalDate.now())) {

                double descuento = totalGeneral * (cupon.getPorcentajeDescuento() / 100.0);

                pedido.setIdCupon(cupon.getIdCupon());
                pedido.setTotal((int) (totalGeneral - descuento));
                return;
            }
        }

        pedido.setTotal((int) totalGeneral);
    }

    private void cargarDireccion(Pedido pedido) {

    if ("ENVIO".equalsIgnoreCase(pedido.getTipoEntrega())) {

        Direccion direccion = direccionRepository
                .findById(pedido.getIdDireccion())
                .orElse(null);

        if (direccion != null) {
            pedido.setDireccionEnvio(
                    direccion.getCalle() + " " + direccion.getNumero()
                    + ", " + direccion.getComuna()
                    + ", " + direccion.getCiudad()
            );
        }

    } else {
        pedido.setDireccionEnvio("Retiro en tienda");
    }
}

    public List<Pedido> listar() {
        return pedidoRepository.findAll();
    }

    public void eliminar(Long id) {
        pedidoRepository.deleteById(id);
    }

    public Optional<Pedido> findById(Long id) {
        return pedidoRepository.findById(id);
}
}
