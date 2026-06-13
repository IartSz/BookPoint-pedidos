package com.BookPoint.pedidos.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.BookPoint.pedidos.model.CarroDTO;
import com.BookPoint.pedidos.model.CuponDescuentoDTO;
import com.BookPoint.pedidos.model.Direccion;
import com.BookPoint.pedidos.model.ItemCarroDTO;
import com.BookPoint.pedidos.model.Pedido;
import com.BookPoint.pedidos.model.UsuarioDTO;
import com.BookPoint.pedidos.repository.DireccionRepository;
import com.BookPoint.pedidos.repository.PedidoRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;

import org.springframework.http.HttpHeaders;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private DireccionRepository direccionRepository;

    @InjectMocks
    private PedidoService pedidoService;

    @Test
    void testCrearCarro() {
        ItemCarroDTO itemCarro = new ItemCarroDTO();
        itemCarro.setIdProducto(10L);
        itemCarro.setNombreProducto("Libro");
        itemCarro.setCantidad(2);
        itemCarro.setPrecioUnitario(5000.0);
        itemCarro.setSubtotal(10000.0);

        CarroDTO carro = new CarroDTO();
        carro.setIdUsuario(1L);
        carro.setCodigoCupon(null); // sin cupón
        carro.setTipoEntrega("RETIRO"); // retiro en tienda
        carro.setItems(new ArrayList<>(List.of(itemCarro)));

        UsuarioDTO usuario = new UsuarioDTO();
        usuario.setNombre("Juan");
        usuario.setApellido("Perez");

        when(restTemplate.getForObject(anyString(), eq(CarroDTO.class))).thenReturn(carro);
        when(restTemplate.getForObject(anyString(), eq(UsuarioDTO.class))).thenReturn(usuario);
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        Pedido resultado = pedidoService.crearCarro(1L);

        assertNotNull(resultado);
        assertEquals("Juan Perez", resultado.getNombreCliente());
        assertEquals(1L, resultado.getIdUsuario());
        assertEquals(10000.0, resultado.getSubtotal());
        assertEquals(10000, resultado.getTotal()); // int, sin descuento
        assertEquals("Retiro en tienda", resultado.getDireccionEnvio());
        assertEquals(1, resultado.getItems().size());
    }

    @Test
    void testFindByIdExiste() {
        Pedido pedido = new Pedido();
        pedido.setIdPedido(1L);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertTrue(pedidoService.findById(1L).isPresent());
    }

    @Test
    void testFindByIdNoExiste() {
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());
        assertTrue(pedidoService.findById(99L).isEmpty());
    }

    @Test
    void testListar() {
        when(pedidoRepository.findAll()).thenReturn(List.of(new Pedido(), new Pedido()));
        assertEquals(2, pedidoService.listar().size());
    }

    @Test
    void testEliminar() {
        pedidoService.eliminar(1L);
        verify(pedidoRepository, times(1)).deleteById(1L);
    }

    @Test
    void testCrearCarroConCuponValido() {
        ItemCarroDTO itemCarro = new ItemCarroDTO();
        itemCarro.setIdProducto(10L);
        itemCarro.setNombreProducto("Libro");
        itemCarro.setCantidad(2);
        itemCarro.setPrecioUnitario(5000.0);
        itemCarro.setSubtotal(10000.0);

        CarroDTO carro = new CarroDTO();
        carro.setIdUsuario(1L);
        carro.setCodigoCupon("DESC10");
        carro.setTipoEntrega("RETIRO");
        carro.setItems(new ArrayList<>(List.of(itemCarro)));

        UsuarioDTO usuario = new UsuarioDTO();
        usuario.setNombre("Juan");
        usuario.setApellido("Perez");

        CuponDescuentoDTO cupon = new CuponDescuentoDTO();
        cupon.setIdCupon(1L);
        cupon.setActivo(true);
        cupon.setFechaExpiracion(LocalDate.now().plusDays(5));
        cupon.setPorcentajeDescuento(10.0);

        when(restTemplate.getForObject(anyString(), eq(CarroDTO.class))).thenReturn(carro);
        when(restTemplate.getForObject(anyString(), eq(UsuarioDTO.class))).thenReturn(usuario);
        when(restTemplate.getForObject(anyString(), eq(CuponDescuentoDTO.class))).thenReturn(cupon);
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        Pedido resultado = pedidoService.crearCarro(1L);

        assertEquals(10000.0, resultado.getSubtotal());
        assertEquals(9000, resultado.getTotal()); // 10000 - 1000 (10%), casteado a int
        assertEquals(1L, resultado.getIdCupon());
    }

    @Test
    void testCrearCarroConEnvio() {
        ItemCarroDTO itemCarro = new ItemCarroDTO();
        itemCarro.setIdProducto(10L);
        itemCarro.setNombreProducto("Libro");
        itemCarro.setCantidad(1);
        itemCarro.setPrecioUnitario(5000.0);
        itemCarro.setSubtotal(5000.0);

        CarroDTO carro = new CarroDTO();
        carro.setIdUsuario(1L);
        carro.setCodigoCupon(null);
        carro.setTipoEntrega("ENVIO");
        carro.setIdDireccion(7L);
        carro.setItems(new ArrayList<>(List.of(itemCarro)));

        UsuarioDTO usuario = new UsuarioDTO();
        usuario.setNombre("Juan");
        usuario.setApellido("Perez");

        Direccion direccion = new Direccion();
        direccion.setCalle("Av. Siempre Viva");
        direccion.setNumero("742");
        direccion.setComuna("Springfield");
        direccion.setCiudad("Santiago");

        when(restTemplate.getForObject(anyString(), eq(CarroDTO.class))).thenReturn(carro);
        when(restTemplate.getForObject(anyString(), eq(UsuarioDTO.class))).thenReturn(usuario);
        when(direccionRepository.findById(7L)).thenReturn(Optional.of(direccion));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        Pedido resultado = pedidoService.crearCarro(1L);

        assertEquals("Av. Siempre Viva 742, Springfield, Santiago", resultado.getDireccionEnvio());
    }

    @Test
    void testCrearCarroStockInsuficiente() {
        ItemCarroDTO itemCarro = new ItemCarroDTO();
        itemCarro.setIdProducto(10L);
        itemCarro.setNombreProducto("Libro");
        itemCarro.setCantidad(2);
        itemCarro.setPrecioUnitario(5000.0);
        itemCarro.setSubtotal(10000.0);

        CarroDTO carro = new CarroDTO();
        carro.setIdUsuario(1L);
        carro.setTipoEntrega("RETIRO");
        carro.setItems(new ArrayList<>(List.of(itemCarro)));

        UsuarioDTO usuario = new UsuarioDTO();
        usuario.setNombre("Juan");
        usuario.setApellido("Perez");

        when(restTemplate.getForObject(anyString(), eq(CarroDTO.class))).thenReturn(carro);
        when(restTemplate.getForObject(anyString(), eq(UsuarioDTO.class))).thenReturn(usuario);

        doThrow(HttpClientErrorException.create(HttpStatus.CONFLICT, "Conflict", new HttpHeaders(), new byte[0], null))
                .when(restTemplate).put(anyString(), isNull());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> pedidoService.crearCarro(1L));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    @Test
    void testCrearCarroConCuponVacio() {
        Long idCarro = 1L;

        ItemCarroDTO itemCarro = new ItemCarroDTO();
        itemCarro.setIdProducto(1L);
        itemCarro.setNombreProducto("Libro Java");
        itemCarro.setCantidad(2);
        itemCarro.setPrecioUnitario(1000.0);
        itemCarro.setSubtotal(2000.0);

        CarroDTO carro = new CarroDTO();
        carro.setIdCarro(idCarro);
        carro.setIdUsuario(1L);
        carro.setCodigoCupon("");
        carro.setTipoEntrega("RETIRO");
        carro.setItems(List.of(itemCarro));

        UsuarioDTO usuario = new UsuarioDTO();
        usuario.setNombre("Juan");
        usuario.setApellido("Perez");

        when(restTemplate.getForObject(
                "http://localhost:8082/api/v1/carrito/" + idCarro,
                CarroDTO.class)).thenReturn(carro);

        when(restTemplate.getForObject(
                "http://localhost:8083/api/v1/usuarios/1",
                UsuarioDTO.class)).thenReturn(usuario);

        doNothing().when(restTemplate).put(
                "http://localhost:8091/api/v1/inventario/descontar/1/2",
                null);

        when(pedidoRepository.save(any(Pedido.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Pedido resultado = pedidoService.crearCarro(idCarro);

        assertNotNull(resultado);
        assertEquals(2000, resultado.getTotal());
        assertEquals("", resultado.getCodigoCupon());

        verify(restTemplate, never()).getForObject(
                contains("http://localhost:8085/api/v1/cupones/"),
                eq(CuponDescuentoDTO.class));

        verify(pedidoRepository, times(2)).save(any(Pedido.class));
    }

    @Test
    void testCrearCarroConCuponInactivo() {
        Long idCarro = 1L;

        ItemCarroDTO itemCarro = new ItemCarroDTO();
        itemCarro.setIdProducto(1L);
        itemCarro.setNombreProducto("Libro Java");
        itemCarro.setCantidad(2);
        itemCarro.setPrecioUnitario(1000.0);
        itemCarro.setSubtotal(2000.0);

        CarroDTO carro = new CarroDTO();
        carro.setIdCarro(idCarro);
        carro.setIdUsuario(1L);
        carro.setCodigoCupon("DESC10");
        carro.setTipoEntrega("RETIRO");
        carro.setItems(List.of(itemCarro));

        UsuarioDTO usuario = new UsuarioDTO();
        usuario.setNombre("Juan");
        usuario.setApellido("Perez");

        CuponDescuentoDTO cupon = new CuponDescuentoDTO();
        cupon.setIdCupon(1L);
        cupon.setCodigo("DESC10");
        cupon.setPorcentajeDescuento(10.0);
        cupon.setActivo(false);
        cupon.setFechaExpiracion(LocalDate.now().plusDays(5));

        when(restTemplate.getForObject("http://localhost:8082/api/v1/carrito/" + idCarro, CarroDTO.class))
                .thenReturn(carro);
        when(restTemplate.getForObject("http://localhost:8083/api/v1/usuarios/1", UsuarioDTO.class))
                .thenReturn(usuario);
        when(restTemplate.getForObject("http://localhost:8085/api/v1/cupones/DESC10", CuponDescuentoDTO.class))
                .thenReturn(cupon);

        doNothing().when(restTemplate).put("http://localhost:8091/api/v1/inventario/descontar/1/2", null);

        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Pedido resultado = pedidoService.crearCarro(idCarro);

        assertNotNull(resultado);
        assertEquals(2000, resultado.getTotal());
        assertNull(resultado.getIdCupon());

        verify(pedidoRepository, times(2)).save(any(Pedido.class));
    }

    @Test
    void testCrearCarroConCuponVencido() {
        Long idCarro = 1L;

        ItemCarroDTO itemCarro = new ItemCarroDTO();
        itemCarro.setIdProducto(1L);
        itemCarro.setNombreProducto("Libro Java");
        itemCarro.setCantidad(2);
        itemCarro.setPrecioUnitario(1000.0);
        itemCarro.setSubtotal(2000.0);

        CarroDTO carro = new CarroDTO();
        carro.setIdCarro(idCarro);
        carro.setIdUsuario(1L);
        carro.setCodigoCupon("DESC10");
        carro.setTipoEntrega("RETIRO");
        carro.setItems(List.of(itemCarro));

        UsuarioDTO usuario = new UsuarioDTO();
        usuario.setNombre("Juan");
        usuario.setApellido("Perez");

        CuponDescuentoDTO cupon = new CuponDescuentoDTO();
        cupon.setIdCupon(1L);
        cupon.setCodigo("DESC10");
        cupon.setPorcentajeDescuento(10.0);
        cupon.setActivo(true);
        cupon.setFechaExpiracion(LocalDate.now().minusDays(1));

        when(restTemplate.getForObject("http://localhost:8082/api/v1/carrito/" + idCarro, CarroDTO.class))
                .thenReturn(carro);
        when(restTemplate.getForObject("http://localhost:8083/api/v1/usuarios/1", UsuarioDTO.class))
                .thenReturn(usuario);
        when(restTemplate.getForObject("http://localhost:8085/api/v1/cupones/DESC10", CuponDescuentoDTO.class))
                .thenReturn(cupon);

        doNothing().when(restTemplate).put("http://localhost:8091/api/v1/inventario/descontar/1/2", null);

        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Pedido resultado = pedidoService.crearCarro(idCarro);

        assertNotNull(resultado);
        assertEquals(2000, resultado.getTotal());
        assertNull(resultado.getIdCupon());

        verify(pedidoRepository, times(2)).save(any(Pedido.class));
    }

    @Test
    void testCrearCarroConCuponNullDesdeServicio() {
        Long idCarro = 1L;

        ItemCarroDTO itemCarro = new ItemCarroDTO();
        itemCarro.setIdProducto(1L);
        itemCarro.setNombreProducto("Libro Java");
        itemCarro.setCantidad(2);
        itemCarro.setPrecioUnitario(1000.0);
        itemCarro.setSubtotal(2000.0);

        CarroDTO carro = new CarroDTO();
        carro.setIdCarro(idCarro);
        carro.setIdUsuario(1L);
        carro.setCodigoCupon("DESC10");
        carro.setTipoEntrega("RETIRO");
        carro.setItems(List.of(itemCarro));

        UsuarioDTO usuario = new UsuarioDTO();
        usuario.setNombre("Juan");
        usuario.setApellido("Perez");

        when(restTemplate.getForObject("http://localhost:8082/api/v1/carrito/" + idCarro, CarroDTO.class))
                .thenReturn(carro);
        when(restTemplate.getForObject("http://localhost:8083/api/v1/usuarios/1", UsuarioDTO.class))
                .thenReturn(usuario);
        when(restTemplate.getForObject("http://localhost:8085/api/v1/cupones/DESC10", CuponDescuentoDTO.class))
                .thenReturn(null);

        doNothing().when(restTemplate).put("http://localhost:8091/api/v1/inventario/descontar/1/2", null);

        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Pedido resultado = pedidoService.crearCarro(idCarro);

        assertNotNull(resultado);
        assertEquals(2000, resultado.getTotal());
        assertNull(resultado.getIdCupon());

        verify(pedidoRepository, times(2)).save(any(Pedido.class));
    }

    @Test
    void testCrearCarroConDireccionEnvio() {
        Long idCarro = 1L;

        ItemCarroDTO itemCarro = new ItemCarroDTO();
        itemCarro.setIdProducto(1L);
        itemCarro.setNombreProducto("Libro Java");
        itemCarro.setCantidad(2);
        itemCarro.setPrecioUnitario(1000.0);
        itemCarro.setSubtotal(2000.0);

        CarroDTO carro = new CarroDTO();
        carro.setIdCarro(idCarro);
        carro.setIdUsuario(1L);
        carro.setCodigoCupon(null);
        carro.setTipoEntrega("ENVIO");
        carro.setIdDireccion(1L);
        carro.setItems(List.of(itemCarro));

        UsuarioDTO usuario = new UsuarioDTO();
        usuario.setNombre("Juan");
        usuario.setApellido("Perez");

        Direccion direccion = new Direccion();
        direccion.setIdDireccion(1L);
        direccion.setCalle("Los Robles");
        direccion.setNumero("123");
        direccion.setComuna("Maipu");
        direccion.setCiudad("Santiago");

        when(restTemplate.getForObject("http://localhost:8082/api/v1/carrito/" + idCarro, CarroDTO.class))
                .thenReturn(carro);
        when(restTemplate.getForObject("http://localhost:8083/api/v1/usuarios/1", UsuarioDTO.class))
                .thenReturn(usuario);

        when(direccionRepository.findById(1L)).thenReturn(Optional.of(direccion));

        doNothing().when(restTemplate).put("http://localhost:8091/api/v1/inventario/descontar/1/2", null);

        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Pedido resultado = pedidoService.crearCarro(idCarro);

        assertNotNull(resultado);
        assertEquals("Los Robles 123, Maipu, Santiago", resultado.getDireccionEnvio());
        assertEquals(2000, resultado.getTotal());

        verify(direccionRepository, times(1)).findById(1L);
        verify(pedidoRepository, times(2)).save(any(Pedido.class));
    }

    @Test
    void testCrearCarroConDireccionEnvioNoEncontrada() {
        Long idCarro = 1L;

        ItemCarroDTO itemCarro = new ItemCarroDTO();
        itemCarro.setIdProducto(1L);
        itemCarro.setNombreProducto("Libro Java");
        itemCarro.setCantidad(2);
        itemCarro.setPrecioUnitario(1000.0);
        itemCarro.setSubtotal(2000.0);

        CarroDTO carro = new CarroDTO();
        carro.setIdCarro(idCarro);
        carro.setIdUsuario(1L);
        carro.setCodigoCupon(null);
        carro.setTipoEntrega("ENVIO");
        carro.setIdDireccion(99L);
        carro.setItems(List.of(itemCarro));

        UsuarioDTO usuario = new UsuarioDTO();
        usuario.setNombre("Juan");
        usuario.setApellido("Perez");

        when(restTemplate.getForObject("http://localhost:8082/api/v1/carrito/" + idCarro, CarroDTO.class))
                .thenReturn(carro);
        when(restTemplate.getForObject("http://localhost:8083/api/v1/usuarios/1", UsuarioDTO.class))
                .thenReturn(usuario);

        when(direccionRepository.findById(99L)).thenReturn(Optional.empty());

        doNothing().when(restTemplate).put("http://localhost:8091/api/v1/inventario/descontar/1/2", null);

        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Pedido resultado = pedidoService.crearCarro(idCarro);

        assertNotNull(resultado);
        assertNull(resultado.getDireccionEnvio());
        assertEquals(2000, resultado.getTotal());

        verify(direccionRepository, times(1)).findById(99L);
        verify(pedidoRepository, times(2)).save(any(Pedido.class));
    }
}
