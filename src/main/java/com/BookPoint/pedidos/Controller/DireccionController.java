package com.BookPoint.pedidos.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.BookPoint.pedidos.model.Direccion;
import com.BookPoint.pedidos.service.DireccionService;

@RestController
@RequestMapping("api/direcciones")
public class DireccionController {
    @Autowired
    private DireccionService direccionService;

    @PostMapping
    public ResponseEntity<?> postDireccion(@RequestBody Direccion direccion){
        try{
            Direccion nueva = direccionService.guardarDireccion(direccion);
            if(nueva == null){
                return new ResponseEntity<>("No se pudo crear la direccion", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(nueva, HttpStatus.CREATED);
        } catch (RuntimeException e){
            return new ResponseEntity<>("Error al crear la direccion", HttpStatus.CONFLICT);
        }
    }
}
