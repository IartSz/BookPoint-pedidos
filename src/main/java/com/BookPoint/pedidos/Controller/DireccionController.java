package com.BookPoint.pedidos.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.BookPoint.pedidos.model.Direccion;
import com.BookPoint.pedidos.service.DireccionService;

@RestController
@RequestMapping("api/v1/direcciones")
public class DireccionController {
    @Autowired
    private DireccionService direccionService;

    @GetMapping
    public ResponseEntity<?> getDirecciones(){
        try{
            List<Direccion> direcciones = direccionService.listarDirecciones();
            if(direcciones.isEmpty()){
                return new ResponseEntity<>("No hay direcciones registradas", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(direcciones, HttpStatus.OK);
        } catch (RuntimeException e){
            return new ResponseEntity<>("Error al obtener direcciones", HttpStatus.CONFLICT);
        }
    }

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
