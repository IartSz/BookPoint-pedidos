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

    public List<Direccion> listarDirecciones(){
        return direccionRepository.findAll();
    }

    public Optional<Direccion> findById(Long id){
        return direccionRepository.findById(id);
    }

    public void eliminarDireccion(Long id){
        direccionRepository.deleteById(id);
    }

    public Direccion actualizar(Long id, Direccion direccion){
        Direccion existente = direccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Direccion no encontrada"));
        existente.setCalle(direccion.getCalle());
        existente.setNumero(direccion.getNumero()); 
        
        return direccionRepository.save(existente);
    }
}
