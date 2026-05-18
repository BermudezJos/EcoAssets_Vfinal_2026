package com.ecommerce.electronicos.repository;

import com.ecommerce.electronicos.model.Dispositivo;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DispositivoRepository extends MongoRepository<Dispositivo, String> {

    // Búsqueda de vendedor por ID
    @Query("{ 'vendedor._id' : ?0 }")
    List<Dispositivo> findByVendedorId(String vendedorId);
 
    //busqueda por categoria
    List<Dispositivo> findByCategoriaIgnoreCase(String categoria);
 
    //busqueda por nombre o marca
    @Query("{ '$or': [ " +
           "{ 'nombre': { '$regex': ?0, '$options': 'i' } }, " +
           "{ 'marca':  { '$regex': ?0, '$options': 'i' } }, " +
           "{ 'modelo': { '$regex': ?0, '$options': 'i' } } " +
           "] }")
    List<Dispositivo> searchByNombre(String query);

    // Solo dispositivos que NO han sido vendidos
    List<Dispositivo> findByDisponibleTrue();

    // Si usas categorías, también debes filtrarlas
    List<Dispositivo> findByCategoriaAndDisponibleTrue(String categoria);
}