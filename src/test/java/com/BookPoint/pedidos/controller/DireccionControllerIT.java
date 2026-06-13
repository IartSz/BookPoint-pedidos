package com.BookPoint.pedidos.controller;

import com.BookPoint.pedidos.model.Direccion;
import com.BookPoint.pedidos.repository.DireccionRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DireccionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DireccionRepository direccionRepository;

    @MockitoBean
    private RestTemplate restTemplate;

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void cleanDb() {
        direccionRepository.deleteAll();
    }

    @Test
    void testCrearYObtenerDireccion() throws Exception {
        Direccion direccion = new Direccion();
        direccion.setIdUsuario(1L);
        direccion.setCalle("Los Robles");
        direccion.setNumero("123");
        direccion.setRegion("Metropolitana");
        direccion.setCiudad("Santiago");
        direccion.setComuna("Maipu");
        direccion.setCodigoPostal(9250000);

        mockMvc.perform(post("/api/v1/direcciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(direccion)))
                .andExpect(status().isCreated())
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
    }

    @Test
    void testObtenerDireccionPorId() throws Exception {
        Direccion direccion = new Direccion();
        direccion.setIdUsuario(1L);
        direccion.setCalle("Los Robles");
        direccion.setNumero("123");
        direccion.setRegion("Metropolitana");
        direccion.setCiudad("Santiago");
        direccion.setComuna("Maipu");
        direccion.setCodigoPostal(9250000);

        Direccion guardada = direccionRepository.save(direccion);

        mockMvc.perform(get("/api/v1/direcciones/{id}", guardada.getIdDireccion()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idDireccion").value(guardada.getIdDireccion()))
                .andExpect(jsonPath("$.idUsuario").value(1))
                .andExpect(jsonPath("$.calle").value("Los Robles"))
                .andExpect(jsonPath("$.numero").value("123"))
                .andExpect(jsonPath("$.region").value("Metropolitana"))
                .andExpect(jsonPath("$.ciudad").value("Santiago"))
                .andExpect(jsonPath("$.comuna").value("Maipu"))
                .andExpect(jsonPath("$.codigoPostal").value(9250000))
                .andExpect(jsonPath("$._links.self").exists())
                .andExpect(jsonPath("$._links.direcciones").exists());
    }

    @Test
    void testActualizarDireccion() throws Exception {
        Direccion direccion = new Direccion();
        direccion.setIdUsuario(1L);
        direccion.setCalle("Los Robles");
        direccion.setNumero("123");
        direccion.setRegion("Metropolitana");
        direccion.setCiudad("Santiago");
        direccion.setComuna("Maipu");
        direccion.setCodigoPostal(9250000);

        Direccion guardada = direccionRepository.save(direccion);

        Direccion datos = new Direccion();
        datos.setIdUsuario(1L);
        datos.setCalle("Nueva Calle");
        datos.setNumero("456");
        datos.setRegion("Valparaiso");
        datos.setCiudad("Viña del Mar");
        datos.setComuna("Reñaca");
        datos.setCodigoPostal(2520000);

        mockMvc.perform(put("/api/v1/direcciones/" + guardada.getIdDireccion())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(datos)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.calle").value("Nueva Calle"))
                .andExpect(jsonPath("$.numero").value("456"))
                .andExpect(jsonPath("$.region").value("Metropolitana"))
                .andExpect(jsonPath("$.ciudad").value("Santiago"))
                .andExpect(jsonPath("$.comuna").value("Maipu"))
                .andExpect(jsonPath("$.codigoPostal").value(9250000))
                .andExpect(jsonPath("$._links").exists());
    }

    @Test
    void testEliminarDireccion() throws Exception {
        Direccion direccion = new Direccion();
        direccion.setIdUsuario(1L);
        direccion.setCalle("Los Robles");
        direccion.setNumero("123");
        direccion.setRegion("Metropolitana");
        direccion.setCiudad("Santiago");
        direccion.setComuna("Maipu");
        direccion.setCodigoPostal(9250000);

        Direccion guardada = direccionRepository.save(direccion);

        mockMvc.perform(delete("/api/v1/direcciones/{id}", guardada.getIdDireccion()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/direcciones/" + guardada.getIdDireccion()))
                .andExpect(status().isNotFound());
    }
}
