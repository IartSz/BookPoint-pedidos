package com.BookPoint.pedidos.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.BookPoint.pedidos.model.Direccion;
import com.BookPoint.pedidos.repository.DireccionRepository;

@Service
public class DireccionService {
    @Autowired
    private DireccionRepository direccionRepository;

    public Direccion guardarDireccion(Direccion direccion){
        return direccionRepository.save(direccion);
    }

    public List<Direccion> listarDireccion(){
        return direccionRepository.findAll();
    }

    public Optional<Direccion> findById(Long id){
        return direccionRepository.findById(id);
    }
}
