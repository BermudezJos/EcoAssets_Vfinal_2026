package com.ecommerce.electronicos.service;

import com.ecommerce.electronicos.model.Dispositivo;
import com.ecommerce.electronicos.model.Usuario;
import com.ecommerce.electronicos.repository.DispositivoRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@Service
public class DispositivoService {
    
    private final DispositivoRepository dispositivoRepository;
    private UsuarioService usuarioService;
    
    public DispositivoService(DispositivoRepository dispositivoRepository, UsuarioService usuarioService) {
        this.dispositivoRepository = dispositivoRepository;
        this.usuarioService = usuarioService;
    }
    
    public Dispositivo crearDispositivo(Dispositivo dispositivo) {
    // 1. Guardamos el dispositivo primero
    Dispositivo guardado = dispositivoRepository.save(dispositivo);
    
    // 2. Sincronizar con la lista del usuario
    if (guardado.getVendedor() != null) {
        Usuario vendedor = guardado.getVendedor();
        
        // Inicializar la lista si es nula para evitar NullPointerException
        if (vendedor.getDispositivosPublicados() == null) {
            vendedor.setDispositivosPublicados(new ArrayList<>());
        }

        if (!vendedor.getDispositivosPublicados().contains(guardado)) {
            vendedor.getDispositivosPublicados().add(guardado);
            // IMPORTANTE: Debes guardar al usuario para que MongoDB registre el cambio en el array
            usuarioService.actualizarUsuario(vendedor); 
        }
    }
    return guardado;
}
    public Dispositivo actualizarDispositivo(Dispositivo dispositivo) {
        return dispositivoRepository.save(dispositivo);
    }
    
    public List<Dispositivo> obtenerTodos() {
        return dispositivoRepository.findAll();
    }
    
    public Optional<Dispositivo> obtenerPorId(String id) {
        return dispositivoRepository.findById(id);
    }
    
    public void eliminarDispositivo(String id) {
        dispositivoRepository.deleteById(id);
    }
    
    public List<Dispositivo> obtenerPorVendedor(String vendedorId) {
        return dispositivoRepository.findByVendedorId(vendedorId);
    }
    
    public List<Dispositivo> buscarPorCategoria(String categoria) {
        return dispositivoRepository.findByCategoriaIgnoreCase(categoria);
    }
    
    public List<Dispositivo> buscarPorNombre(String query) {
        return dispositivoRepository.searchByNombre(query);
    }
    
    public List<String> obtenerCategorias() {
        return obtenerTodos().stream()
                .map(Dispositivo::getCategoria)
                .distinct()
                .toList();
    }

    @Autowired
    private MongoTemplate mongoTemplate;

    // Súper método para búsqueda facetada (filtros dinámicos)
    public List<Dispositivo> buscarDispositivosConFiltros(
            String categoria, Double precioMin, Double precioMax, 
            String departamento, String municipio, String marca, 
            String ordenPrecio) {

        Query query = new Query();

        query.addCriteria(Criteria.where("disponible").is(true));
        
        // 1. Filtro por categoría (si el usuario la seleccionó)
        if (categoria != null && !categoria.isEmpty()) {
            query.addCriteria(Criteria.where("categoria").is(categoria));
        }

        // 2. Filtro por marca (la "i" es para ignorar mayúsculas/minúsculas)
        if (marca != null && !marca.isEmpty()) {
            query.addCriteria(Criteria.where("marca").regex(marca, "i"));
        }

        // 3. Filtros por Ubicación
        if (departamento != null && !departamento.isEmpty()) {
            query.addCriteria(Criteria.where("departamento").is(departamento));
        }
        if (municipio != null && !municipio.isEmpty()) {
            query.addCriteria(Criteria.where("municipio").regex(municipio, "i"));
        }

        // 4. Filtro por rango de Precios (Ej: Menos de 50mil, o entre 50 y 100mil)
        if (precioMin != null || precioMax != null) {
            Criteria criteriaPrecio = Criteria.where("precio");
            if (precioMin != null) {
                criteriaPrecio.gte(precioMin); // Mayor o igual a (Greater Than or Equal)
            }
            if (precioMax != null) {
                criteriaPrecio.lte(precioMax); // Menor o igual a (Less Than or Equal)
            }
            query.addCriteria(criteriaPrecio);
        }

        // 5. Ordenamiento de productos (De menor a mayor o viceversa)
        if (ordenPrecio != null && !ordenPrecio.isEmpty()) {
            if (ordenPrecio.equalsIgnoreCase("asc")) {
                query.with(Sort.by(Sort.Direction.ASC, "precio")); // Más baratos primero
            } else if (ordenPrecio.equalsIgnoreCase("desc")) {
                query.with(Sort.by(Sort.Direction.DESC, "precio")); // Más caros primero
            }
        }

        // Ejecuta la consulta combinada y devuelve la lista de dispositivos
        return mongoTemplate.find(query, Dispositivo.class);
    }

    public List<Dispositivo> obtenerTodosDisponibles() {
        return dispositivoRepository.findByDisponibleTrue();
    }

    public List<Dispositivo> obtenerPorCategoria(String categoria) {
    
        return dispositivoRepository.findByCategoriaAndDisponibleTrue(categoria);
    }

    
}