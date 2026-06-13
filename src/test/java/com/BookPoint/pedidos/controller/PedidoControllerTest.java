package com.BookPoint.pedidos.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Optional;

import com.BookPoint.pedidos.Controller.PedidoController;
import com.BookPoint.pedidos.enums.EstadoPedido;
import com.BookPoint.pedidos.model.Pedido;
import com.BookPoint.pedidos.service.PedidoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.test.context.ActiveProfiles;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PedidoController.class)
@ActiveProfiles("test")
public class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PedidoService pedidoService;

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    void testCheckoutYObtenerPedidos() throws Exception {
        Pedido pedido = new Pedido();
        pedido.setIdPedido(1L);
        pedido.setIdUsuario(1L);
        pedido.setNombreCliente("Juan Perez");
        pedido.setEstadoPedido(EstadoPedido.RECIBIDO);
        pedido.setTipoEntrega("RETIRO");
        pedido.setDireccionEnvio("Retiro en tienda");
        pedido.setSubtotal(20000.0);
        pedido.setTotal(20000);
        pedido.setMensajeConfirmacion("Gracias por tu compra, Juan Perez. Tu pedido fue recibido correctamente.");

        when(pedidoService.crearCarro(1L)).thenReturn(pedido);
        when(pedidoService.listar()).thenReturn(List.of(pedido));

        mockMvc.perform(post("/api/v1/pedidos/checkout/{idCarro}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cliente").value("Juan Perez"))
                .andExpect(jsonPath("$.tipoEntrega").value("RETIRO"))
                .andExpect(jsonPath("$.direccionEnvio").value("Retiro en tienda"))
                .andExpect(jsonPath("$.subtotal").value(20000.0))
                .andExpect(jsonPath("$.total").value(20000))
                .andExpect(jsonPath("$.mensajeConfirmacion")
                        .value("Gracias por tu compra, Juan Perez. Tu pedido fue recibido correctamente."))
                .andExpect(jsonPath("$._links.self").exists())
                .andExpect(jsonPath("$._links.pedidos").exists());

        mockMvc.perform(get("/api/v1/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.pedidoList[0].cliente").value("Juan Perez"))
                .andExpect(jsonPath("$._embedded.pedidoList[0].total").value(20000))
                .andExpect(jsonPath("$._links.self").exists());

        verify(pedidoService, times(1)).crearCarro(1L);
        verify(pedidoService, times(1)).listar();
    }

    @Test
    void testObtenerPedidoPorId() throws Exception {
        Pedido pedido = new Pedido();
        pedido.setIdPedido(1L);
        pedido.setIdUsuario(1L);
        pedido.setNombreCliente("Juan Perez");
        pedido.setEstadoPedido(EstadoPedido.RECIBIDO);
        pedido.setTipoEntrega("RETIRO");
        pedido.setDireccionEnvio("Retiro en tienda");
        pedido.setSubtotal(20000.0);
        pedido.setTotal(20000);

        when(pedidoService.findById(1L)).thenReturn(Optional.of(pedido));

        mockMvc.perform(get("/api/v1/pedidos/{id}", pedido.getIdPedido()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPedido").value(pedido.getIdPedido()))
                .andExpect(jsonPath("$.cliente").value("Juan Perez"))
                .andExpect(jsonPath("$.tipoEntrega").value("RETIRO"))
                .andExpect(jsonPath("$.direccionEnvio").value("Retiro en tienda"))
                .andExpect(jsonPath("$.subtotal").value(20000.0))
                .andExpect(jsonPath("$.total").value(20000))
                .andExpect(jsonPath("$._links.self").exists())
                .andExpect(jsonPath("$._links.pedidos").exists());

        verify(pedidoService, times(1)).findById(1L);
    }

    @Test
    void testPedidoNoEncontrado() throws Exception {
        when(pedidoService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/pedidos/{id}", 99L))
                .andExpect(status().isNotFound());

        verify(pedidoService, times(1)).findById(99L);
    }

    @Test
    void testEliminarPedido() throws Exception {
        doNothing().when(pedidoService).eliminar(1L);

        mockMvc.perform(delete("/api/v1/pedidos/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(pedidoService, times(1)).eliminar(1L);
    }

    @Test
    void testCheckoutConError() throws Exception {
        when(pedidoService.crearCarro(anyLong())).thenThrow(new RuntimeException());

        mockMvc.perform(post("/api/v1/pedidos/checkout/{idCarro}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());

        verify(pedidoService, times(1)).crearCarro(1L);
    }

    @Test
    void testCheckoutPedidoNull() throws Exception {
        when(pedidoService.crearCarro(1L)).thenReturn(null);

        mockMvc.perform(post("/api/v1/pedidos/checkout/{idCarro}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(pedidoService, times(1)).crearCarro(1L);
    }

    @Test
    void testCheckoutConRuntimeException() throws Exception {
        when(pedidoService.crearCarro(1L)).thenThrow(new RuntimeException());

        mockMvc.perform(post("/api/v1/pedidos/checkout/{idCarro}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());

        verify(pedidoService, times(1)).crearCarro(1L);
    }
}
