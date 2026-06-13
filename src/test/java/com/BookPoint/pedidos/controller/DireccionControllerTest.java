package com.BookPoint.pedidos.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Optional;

import com.BookPoint.pedidos.Controller.DireccionController;
import com.BookPoint.pedidos.model.Direccion;
import com.BookPoint.pedidos.service.DireccionService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DireccionController.class)
@ActiveProfiles("test")
public class DireccionControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private DireccionService direccionService;

        private ObjectMapper objectMapper = new ObjectMapper();

        @Test
        void testCrearYObtenerDireccion() throws Exception {

                Direccion direccion = new Direccion();
                direccion.setIdDireccion(1L);
                direccion.setIdUsuario(1L);
                direccion.setCalle("Los Robles");
                direccion.setNumero("123");
                direccion.setRegion("Metropolitana");
                direccion.setCiudad("Santiago");
                direccion.setComuna("Maipu");
                direccion.setCodigoPostal(9250000);

                when(direccionService.guardarDireccion(any(Direccion.class)))
                                .thenReturn(direccion);

                when(direccionService.listarDirecciones())
                                .thenReturn(List.of(direccion));

                mockMvc.perform(post("/api/v1/direcciones")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(direccion)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.idDireccion").value(1))
                                .andExpect(jsonPath("$.idUsuario").value(1))
                                .andExpect(jsonPath("$.calle").value("Los Robles"))
                                .andExpect(jsonPath("$.numero").value("123"))
                                .andExpect(jsonPath("$.region").value("Metropolitana"))
                                .andExpect(jsonPath("$.ciudad").value("Santiago"))
                                .andExpect(jsonPath("$.comuna").value("Maipu"))
                                .andExpect(jsonPath("$.codigoPostal").value(9250000))
                                .andExpect(jsonPath("$._links.self").exists())
                                .andExpect(jsonPath("$._links.direcciones").exists());

                mockMvc.perform(get("/api/v1/direcciones"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$._embedded.direccionList[0].idUsuario").value(1))
                                .andExpect(jsonPath("$._embedded.direccionList[0].calle").value("Los Robles"))
                                .andExpect(jsonPath("$._embedded.direccionList[0].numero").value("123"))
                                .andExpect(jsonPath("$._embedded.direccionList[0].region").value("Metropolitana"))
                                .andExpect(jsonPath("$._embedded.direccionList[0].ciudad").value("Santiago"))
                                .andExpect(jsonPath("$._embedded.direccionList[0].comuna").value("Maipu"))
                                .andExpect(jsonPath("$._embedded.direccionList[0].codigoPostal").value(9250000))
                                .andExpect(jsonPath("$._links.self").exists());

                verify(direccionService, times(1)).guardarDireccion(any(Direccion.class));
                verify(direccionService, times(1)).listarDirecciones();
        }

        @Test
        void testObtenerDireccionPorId() throws Exception {

                Direccion direccion = new Direccion();
                direccion.setIdDireccion(1L);
                direccion.setIdUsuario(1L);
                direccion.setCalle("Los Robles");
                direccion.setNumero("123");
                direccion.setRegion("Metropolitana");
                direccion.setCiudad("Santiago");
                direccion.setComuna("Maipu");
                direccion.setCodigoPostal(9250000);

                when(direccionService.findById(1L))
                                .thenReturn(Optional.of(direccion));

                mockMvc.perform(get("/api/v1/direcciones/{id}", 1L))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.idDireccion").value(1))
                                .andExpect(jsonPath("$.idUsuario").value(1))
                                .andExpect(jsonPath("$.calle").value("Los Robles"))
                                .andExpect(jsonPath("$.numero").value("123"))
                                .andExpect(jsonPath("$.region").value("Metropolitana"))
                                .andExpect(jsonPath("$.ciudad").value("Santiago"))
                                .andExpect(jsonPath("$.comuna").value("Maipu"))
                                .andExpect(jsonPath("$.codigoPostal").value(9250000))
                                .andExpect(jsonPath("$._links.self").exists())
                                .andExpect(jsonPath("$._links.direcciones").exists());

                verify(direccionService, times(1)).findById(1L);
        }

        @Test
        void testActualizarDireccion() throws Exception {

                Direccion direccion = new Direccion();
                direccion.setIdDireccion(1L);
                direccion.setIdUsuario(1L);
                direccion.setCalle("Los Robles");
                direccion.setNumero("123");
                direccion.setRegion("Metropolitana");
                direccion.setCiudad("Santiago");
                direccion.setComuna("Maipu");
                direccion.setCodigoPostal(9250000);

                when(direccionService.actualizar(eq(1L), any(Direccion.class)))
                                .thenReturn(direccion);

                mockMvc.perform(put("/api/v1/direcciones/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(direccion)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.idDireccion").value(1))
                                .andExpect(jsonPath("$.idUsuario").value(1))
                                .andExpect(jsonPath("$.calle").value("Los Robles"))
                                .andExpect(jsonPath("$.numero").value("123"))
                                .andExpect(jsonPath("$.region").value("Metropolitana"))
                                .andExpect(jsonPath("$.ciudad").value("Santiago"))
                                .andExpect(jsonPath("$.comuna").value("Maipu"))
                                .andExpect(jsonPath("$.codigoPostal").value(9250000))
                                .andExpect(jsonPath("$._links.self").exists())
                                .andExpect(jsonPath("$._links.direcciones").exists());

                verify(direccionService, times(1))
                                .actualizar(eq(1L), any(Direccion.class));
        }

        @Test
        void testDireccionNoEncontrada() throws Exception {

                when(direccionService.findById(99L))
                                .thenReturn(Optional.empty());

                mockMvc.perform(get("/api/v1/direcciones/{id}", 99L))
                                .andExpect(status().isNotFound());

                verify(direccionService, times(1)).findById(99L);
        }

        @Test
        void testEliminarDireccion() throws Exception {

                doNothing().when(direccionService).eliminarDireccion(1L);

                mockMvc.perform(delete("/api/v1/direcciones/{id}", 1L))
                                .andExpect(status().isNoContent());

                verify(direccionService, times(1)).eliminarDireccion(1L);
        }

        @Test
        void testActualizarDireccionNoEncontrada() throws Exception {

                Direccion direccion = new Direccion();
                direccion.setIdUsuario(1L);
                direccion.setCalle("Nueva Calle");
                direccion.setNumero("456");
                direccion.setRegion("Valparaiso");
                direccion.setCiudad("Viña del Mar");
                direccion.setComuna("Reñaca");
                direccion.setCodigoPostal(2520000);

                when(direccionService.actualizar(eq(99L), any(Direccion.class)))
                                .thenThrow(new RuntimeException("Direccion no encontrada"));

                mockMvc.perform(put("/api/v1/direcciones/{id}", 99L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(direccion)))
                                .andExpect(status().isNotFound());

                verify(direccionService, times(1))
                                .actualizar(eq(99L), any(Direccion.class));
        }
}