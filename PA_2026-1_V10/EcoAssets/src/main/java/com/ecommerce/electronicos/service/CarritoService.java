package com.ecommerce.electronicos.service;

import com.ecommerce.electronicos.model.Dispositivo;
import com.ecommerce.electronicos.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CarritoService {

    @Autowired
    private UsuarioService usuarioService;

    // Método secreto para saber quién es el usuario que está navegando
    private Usuario obtenerUsuarioLogueado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Usuario> usuarioOpt = usuarioService.obtenerPorEmail(email);
        return usuarioOpt.orElse(null);
    }

    public boolean agregarDispositivo(Dispositivo dispositivo) {
        Usuario usuario = obtenerUsuarioLogueado();
        if (usuario == null) return false;

        // Verificamos si este dispositivo ya está en su carrito de la base de datos
        boolean existe = usuario.getCarrito().stream()
                .anyMatch(item -> item.getId().equals(dispositivo.getId()));
        
        if (!existe) {
            usuario.getCarrito().add(dispositivo);
            usuarioService.actualizarUsuario(usuario); // Guardamos permanentemente en MongoDB
            return true;
        }
        return false; 
    }

    public void eliminarDispositivo(String dispositivoId) {
        Usuario usuario = obtenerUsuarioLogueado();
        if (usuario != null) {
            usuario.getCarrito().removeIf(item -> item.getId().equals(dispositivoId));
            usuarioService.actualizarUsuario(usuario); // Actualizamos MongoDB
        }
    }

    public List<Dispositivo> getItems() {
        Usuario usuario = obtenerUsuarioLogueado();
        if (usuario != null && usuario.getCarrito() != null) {
            
            // MAGIA EXTRA: Limpiamos automáticamente los productos 
            // que otro usuario haya comprado mientras este los tenía en el carrito
            List<Dispositivo> itemsValidos = new ArrayList<>();
            boolean necesitaActualizar = false;

            for (Dispositivo d : usuario.getCarrito()) {
                if (d.isDisponible()) {
                    itemsValidos.add(d);
                } else {
                    necesitaActualizar = true; // Un producto se vendió, hay que quitarlo
                }
            }

            if (necesitaActualizar) {
                usuario.setCarrito(itemsValidos);
                usuarioService.actualizarUsuario(usuario);
            }

            return itemsValidos;
        }
        return new ArrayList<>();
    }

    public double obtenerTotal() {
        return getItems().stream().mapToDouble(Dispositivo::getPrecio).sum();
    }

    public void vaciarCarrito() {
        Usuario usuario = obtenerUsuarioLogueado();
        if (usuario != null) {
            usuario.getCarrito().clear();
            usuarioService.actualizarUsuario(usuario);
        }
    }
}