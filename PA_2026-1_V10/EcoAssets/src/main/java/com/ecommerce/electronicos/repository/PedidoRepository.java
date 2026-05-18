package com.ecommerce.electronicos.repository;

import com.ecommerce.electronicos.model.Pedido;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface PedidoRepository extends MongoRepository<Pedido, String> {
    List<Pedido> findByCompradorId(String compradorId);
}