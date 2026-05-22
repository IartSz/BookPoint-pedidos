package com.BookPoint.pedidos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.BookPoint.pedidos.model.Direccion;

@Repository
public interface DireccionRepository extends JpaRepository<Direccion, Long>{
    
}
