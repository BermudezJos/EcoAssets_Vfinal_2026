package com.ecommerce.electronicos.service;

import com.ecommerce.electronicos.model.Pedido;
import com.ecommerce.electronicos.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PedidoService {
    @Autowired
    private PedidoRepository pedidoRepository;

    public Pedido guardarPedido(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    public List<Pedido> obtenerPedidosPorUsuario(String usuarioId) {
        return pedidoRepository.findByCompradorId(usuarioId);
    }
}