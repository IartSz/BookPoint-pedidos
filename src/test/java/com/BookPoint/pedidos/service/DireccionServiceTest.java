package com.BookPoint.pedidos.service;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import com.BookPoint.pedidos.model.Direccion;
import com.BookPoint.pedidos.repository.DireccionRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class DireccionServiceTest {

    @Mock
    private DireccionRepository direccionRepository;

    @InjectMocks
    private DireccionService direccionService;

    @Test
    void testGuardarDireccion() {
        Direccion direccion = new Direccion();
        direccion.setCalle("Av. Siempre Viva");
        direccion.setNumero("742");
        direccion.setComuna("Springfield");
        direccion.setCiudad("Santiago");

        Direccion guardada = new Direccion();
        guardada.setIdDireccion(1L);
        guardada.setCalle("Av. Siempre Viva");
        guardada.setNumero("742");
        guardada.setComuna("Springfield");
        guardada.setCiudad("Santiago");

        when(direccionRepository.save(direccion)).thenReturn(guardada);

        Direccion resultado = direccionService.guardarDireccion(direccion);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdDireccion());
        assertEquals("Av. Siempre Viva", resultado.getCalle());
        verify(direccionRepository).save(direccion);
    }

    @Test
    void testListarDirecciones() {
        when(direccionRepository.findAll()).thenReturn(List.of(new Direccion(), new Direccion()));
        assertEquals(2, direccionService.listarDirecciones().size());
    }

    @Test
    void testFindByIdExiste() {
        Direccion direccion = new Direccion();
        direccion.setIdDireccion(1L);
        when(direccionRepository.findById(1L)).thenReturn(Optional.of(direccion));

        Optional<Direccion> resultado = direccionService.findById(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getIdDireccion());
    }

    @Test
    void testFindByIdNoExiste() {
        when(direccionRepository.findById(99L)).thenReturn(Optional.empty());
        assertTrue(direccionService.findById(99L).isEmpty());
    }

    @Test
    void testActualizar() {
        Direccion existente = new Direccion();
        existente.setIdDireccion(1L);
        existente.setCalle("Calle Vieja");
        existente.setNumero("100");
        existente.setComuna("Comuna Vieja");
        existente.setCiudad("Ciudad Vieja");

        Direccion datos = new Direccion();
        datos.setCalle("Calle Nueva");
        datos.setNumero("200");
        datos.setComuna("Comuna Nueva");
        datos.setCiudad("Ciudad Nueva");

        when(direccionRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(direccionRepository.save(any(Direccion.class))).thenAnswer(inv -> inv.getArgument(0));

        Direccion resultado = direccionService.actualizar(1L, datos);

        assertEquals("Calle Nueva", resultado.getCalle());
        assertEquals("200", resultado.getNumero());
        verify(direccionRepository).save(existente);
    }

    @Test
    void testActualizarNoExistente() {
        Direccion datos = new Direccion();
        when(direccionRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> direccionService.actualizar(99L, datos));

        assertEquals("Direccion no encontrada", ex.getMessage());
        verify(direccionRepository, times(0)).save(any(Direccion.class));
    }

    @Test
    void testEliminarDireccion() {
        direccionService.eliminarDireccion(1L);
        verify(direccionRepository, times(1)).deleteById(1L);
    }
}