package com.ecommerce.electronicos.seguridad;

import com.ecommerce.electronicos.model.TipoUsuario;
import com.ecommerce.electronicos.model.Usuario;
import com.ecommerce.electronicos.service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JwtCookieService jwtCookieService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        
        // Extraemos los datos del perfil de Google
        String email = oauthToken.getPrincipal().getAttribute("email");
        String nombre = oauthToken.getPrincipal().getAttribute("name");

        // Verificamos si el usuario ya existe en nuestra base de datos
        Optional<Usuario> usuarioOpt = usuarioService.obtenerPorEmail(email);
        Usuario usuario;

        if (usuarioOpt.isEmpty()) {
            // Si no existe, le creamos una cuenta automáticamente
            usuario = new Usuario();
            usuario.setEmail(email);
            usuario.setNombre(nombre);
            // Le asignamos una contraseña aleatoria y segura porque usará Google para entrar
            usuario.setPassword(UUID.randomUUID().toString()); 
            usuario.setTipo(TipoUsuario.USUARIO);
            
            // Usamos tu método existente que encripta la contraseña y guarda en MongoDB
            usuarioService.registrarNuevoUsuario(usuario); 
        } else {
            usuario = usuarioOpt.get();
        }

        // Cargar los UserDetails para generar el token JWT (igual que en el AuthController)
        UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getEmail());
        String jwtToken = jwtService.generateToken(userDetails);

        // Añadir el JWT a las cookies del navegador
        jwtCookieService.addJwtCookie(response, jwtToken);

        // Redirigimos a la tienda
        response.sendRedirect("/dispositivos");
    }
}
