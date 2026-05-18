package com.ecommerce.electronicos.controller;

import com.ecommerce.electronicos.model.Usuario;
import com.ecommerce.electronicos.seguridad.JwtCookieService;
import com.ecommerce.electronicos.seguridad.JwtService;
import com.ecommerce.electronicos.service.UsuarioService;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtCookieService jwtCookieService;

    public LoginController(AuthenticationManager authenticationManager, JwtService jwtService,
            JwtCookieService jwtCookieService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.jwtCookieService = jwtCookieService;
    }

    @Autowired
    private UsuarioService usuarioService;
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    //agregado para jwt
    @PostMapping("/login")
    public String login(
                        @RequestParam String username,
                        @RequestParam String password,
                        HttpServletResponse response) {
                            
        Authentication authentication =
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
                
            String token = jwtService.generateToken((UserDetails) authentication.getPrincipal());
            
            jwtCookieService.addJwtCookie(response, token);
            
            return "redirect:/dispositivos";
        }

    @GetMapping("/registro")
    public String mostrarFormularioDeRegistro(Model model) {
        // Le mandamos un usuario vacío al formulario HTML
        model.addAttribute("usuario", new Usuario());
        return "registro"; // Esto abrirá tu archivo registro.html
    }

    @PostMapping("/registro")
    public String procesarRegistro(@ModelAttribute Usuario usuario, RedirectAttributes redirectAttributes) {
        // Guardamos el usuario nuevo
        usuarioService.registrarNuevoUsuario(usuario);
        
        // Redirigimos al login con un mensaje
        redirectAttributes.addFlashAttribute("mensajeExito", "¡Cuenta creada exitosamente! Por favor inicia sesión.");
        return "redirect:/login"; 
    }
}