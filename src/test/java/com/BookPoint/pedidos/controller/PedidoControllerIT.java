package com.BookPoint.pedidos.controller;

import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.BookPoint.pedidos.model.CarroDTO;
import com.BookPoint.pedidos.model.ItemCarroDTO;
import com.BookPoint.pedidos.model.UsuarioDTO;
import com.BookPoint.pedidos.model.Direccion;
import com.BookPoint.pedidos.enums.EstadoPedido;
import com.BookPoint.pedidos.model.Pedido;
import com.BookPoint.pedidos.repository.DireccionRepository;
import com.BookPoint.pedidos.repository.PedidoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.cglib.core.Local;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PedidoControllerIT {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private PedidoRepository pedidoRepository;

        @Autowired
        private DireccionRepository direccionRepository;

        @MockitoBean
        private RestTemplate restTemplate;

        private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        @BeforeEach
        void cleanDb() {
                pedidoRepository.deleteAll();
                direccionRepository.deleteAll();
        }

        @Test
        void testCheckoutYObtenerPedidos() throws Exception {
                Direccion direccion = new Direccion();
                direccion.setIdUsuario(1L);
                direccion.setCalle("Los Robles");
                direccion.setNumero("123");
                direccion.setRegion("Metropolitana");
                direccion.setCiudad("Santiago");
                direccion.setComuna("Maipu");
                direccion.setCodigoPostal(9250000);
                Direccion direccionGuardada = direccionRepository.save(direccion);

                ItemCarroDTO item = new ItemCarroDTO();
                item.setIdProducto(1L);
                item.setNombreProducto("Libro Java");
                item.setCantidad(2);
                item.setPrecioUnitario(10000.);
                item.setSubtotal(2000.0);

                CarroDTO carro = new CarroDTO();
                carro.setIdCarro(1L);
                carro.setIdUsuario(1L);
                carro.setTipoEntrega("ENVIO");
                carro.setIdDireccion(direccionGuardada.getIdDireccion());
                carro.setCodigoCupon(null);
                carro.setItems(List.of(item));

                UsuarioDTO usuario = new UsuarioDTO();
                usuario.setNombre("Juan");
                usuario.setApellido("Perez");

                Mockito.when(restTemplate.getForObject(
                                "http://localhost:8082/api/v1/carrito/1",
                                CarroDTO.class))
                                .thenReturn(carro);

                Mockito.when(restTemplate.getForObject(
                                "http://localhost:8083/api/v1/usuarios/1",
                                UsuarioDTO.class))
                                .thenReturn(usuario);

                Mockito.doNothing().when(restTemplate).put(
                                "http://localhost:8091/api/v1/inventario/descontar/1/2",
                                null);

                mockMvc.perform(post("/api/v1/pedidos/checkout/{idCarro}", 1L)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andDo(print())
                                .andExpect(status().isCreated())
                                .andDo(print())
                                .andExpect(jsonPath("$._links.self").exists())
                                .andExpect(jsonPath("$._links.pedidos").exists());

                mockMvc.perform(get("/api/v1/pedidos"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$._embedded.pedidoList[0].cliente").value("Juan Perez"))
                                .andExpect(jsonPath("$._embedded.pedidoList[0].total").value(2000.0))
                                .andExpect(jsonPath("$._links.self").exists());
        }

        @Test
        void testObtenerPedidoPorId() throws Exception {
                Pedido pedido = new Pedido();
                pedido.setIdUsuario(1L);
                pedido.setNombreCliente("Juan Perez");
                pedido.setEstadoPedido(EstadoPedido.RECIBIDO);
                pedido.setTipoEntrega("RETIRO");
                pedido.setDireccionEnvio("Retiro en tienda");
                pedido.setSubtotal(20000.0);
                pedido.setTotal(20000);
                pedido.setFechaPedido(LocalDate.now());
                Pedido guardado = pedidoRepository.save(pedido);

                mockMvc.perform(get("/api/v1/pedidos/{id}", guardado.getIdPedido()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.idPedido").value(guardado.getIdPedido()))
                                .andExpect(jsonPath("$.cliente").value("Juan Perez"))
                                .andExpect(jsonPath("$.fecha").exists())
                                .andExpect(jsonPath("$.tipoEntrega").value("RETIRO"))
                                .andExpect(jsonPath("$.direccionEnvio").value("Retiro en tienda"))
                                .andExpect(jsonPath("$.subtotal").value(20000.0))
                                .andExpect(jsonPath("$.total").value(20000))
                                .andExpect(jsonPath("$._links.self").exists())
                                .andExpect(jsonPath("$._links.pedidos").exists());
        }

        @Test
        void testEliminarPedido() throws Exception {
                Pedido pedido = new Pedido();
                pedido.setIdUsuario(1L);
                pedido.setNombreCliente("Juan Perez");
                pedido.setFechaPedido(LocalDate.now());
                pedido.setEstadoPedido(EstadoPedido.RECIBIDO);
                pedido.setTipoEntrega("RETIRO");
                pedido.setDireccionEnvio("Retiro en tienda");
                pedido.setSubtotal(20000.0);
                pedido.setTotal(20000);
                Pedido guardado = pedidoRepository.save(pedido);

                mockMvc.perform(delete("/api/v1/pedidos/{id}", guardado.getIdPedido()))
                                .andExpect(status().isNoContent());

                mockMvc.perform(get("/api/v1/pedidos/" + guardado.getIdPedido()))
                                .andExpect(status().isNotFound());
        }
}
