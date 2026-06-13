package com.BookPoint.pedidos.Controller;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;

import com.BookPoint.pedidos.model.Direccion;
import com.BookPoint.pedidos.service.DireccionService;


@RestController
@RequestMapping("/api/v1/direcciones")
public class DireccionController {

    @Autowired
    private DireccionService direccionService;

    @GetMapping
    public CollectionModel<EntityModel<Direccion>> getDirecciones() {
        List<Direccion> direcciones = direccionService.listarDirecciones();
        List<EntityModel<Direccion>> direccionModels = direcciones.stream()
                .map(direccion -> EntityModel.of(direccion,
                        linkTo(methodOn(DireccionController.class).findById(direccion.getIdDireccion())).withSelfRel()))
                .toList();
        return CollectionModel.of(direccionModels,
                linkTo(methodOn(DireccionController.class).getDirecciones()).withSelfRel());
    }

    @PostMapping
    public ResponseEntity<EntityModel<Direccion>> postDireccion(@RequestBody Direccion direccion) {
        Direccion nueva = direccionService.guardarDireccion(direccion);
        EntityModel<Direccion> direccionModel = EntityModel.of(nueva,
                linkTo(methodOn(DireccionController.class).findById(nueva.getIdDireccion())).withSelfRel(),
                linkTo(methodOn(DireccionController.class).getDirecciones()).withRel("direcciones"));
        return ResponseEntity.status(HttpStatus.CREATED).body(direccionModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Direccion>> findById(@PathVariable Long id) {
        return direccionService.findById(id)
                .map(direccion -> ResponseEntity.ok(EntityModel.of(direccion,
                        linkTo(methodOn(DireccionController.class).findById(id)).withSelfRel(),
                        linkTo(methodOn(DireccionController.class).getDirecciones()).withRel("direcciones"))))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Direccion>> actualizar(@PathVariable Long id, @RequestBody Direccion direccion) {
        try {
            Direccion actualizada = direccionService.actualizar(id, direccion);
            return ResponseEntity.ok(EntityModel.of(actualizada,
                    linkTo(methodOn(DireccionController.class).findById(id)).withSelfRel(),
                    linkTo(methodOn(DireccionController.class).getDirecciones()).withRel("direcciones")));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDireccion(@PathVariable Long id) {
        direccionService.eliminarDireccion(id);
        return ResponseEntity.noContent().build();
    }
}