package com.ecommerce.electronicos.seguridad;


import com.ecommerce.electronicos.service.UsuarioService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioService usuarioService;

    public UserDetailsServiceImpl(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var usuarioOpt = usuarioService.obtenerPorEmail(username);
        if (usuarioOpt.isPresent()) {
            var usuario = usuarioOpt.get();
            String role = usuario.getTipo().name(); 
            
            return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPassword())
                .roles(role.replace("ADMINISTRADOR", "ADMIN").replace("USUARIO", "USER"))
                .build();
        }
        throw new UsernameNotFoundException("Usuario no encontrado: " + username);
    }
}
