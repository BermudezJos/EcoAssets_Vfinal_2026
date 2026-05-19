package com.ecommerce.electronicos.controller;

import com.ecommerce.electronicos.model.TipoUsuario;
import com.ecommerce.electronicos.model.Usuario;
import com.ecommerce.electronicos.service.DispositivoService;
import com.ecommerce.electronicos.service.PedidoService;
import com.ecommerce.electronicos.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {
    
    private final UsuarioService usuarioService;
    private final DispositivoService dispositivoService;
    private final PedidoService pedidoService;
    
    public UsuarioController(UsuarioService usuarioService, DispositivoService dispositivoService,
        PedidoService pedidoService) {
        this.usuarioService = usuarioService;
        this.dispositivoService = dispositivoService;
        this.pedidoService = pedidoService;
    }
    
    @GetMapping
    public String listarUsuarios(@RequestParam(defaultValue = "0") int page, Model model) {
        int cantidadPorPagina = 30; // Ajustado a un mínimo de 30 personas por página como solicitaste
        
        Page<Usuario> usuariosPagina = usuarioService.obtenerUsuariosNormalesPagina(page, cantidadPorPagina);
        
        // Enviamos los datos ordenados al HTML
        model.addAttribute("usuarios", usuariosPagina.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", usuariosPagina.getTotalPages());
        
        return "usuarios/lista";
    }
    
    // RUTA 1: Ver el perfil de un usuario específico por ID (Público o Admin)
    @GetMapping("/perfil/{id}")
    public String verPerfilPorId(@PathVariable String id, Model model, Authentication auth) {
        Optional<Usuario> usuarioOpt = usuarioService.obtenerPorId(id);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            model.addAttribute("usuario", usuario);
            model.addAttribute("dispositivos", dispositivoService.obtenerPorVendedor(usuario.getId()));
            
            boolean esPropioPerfil = auth != null && auth.getName().equals(usuario.getEmail());
            model.addAttribute("esPropioPerfil", esPropioPerfil);
            
            boolean esAdmin = auth != null && auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            model.addAttribute("esAdmin", esAdmin);

            if (esPropioPerfil) {
                model.addAttribute("pedidos", pedidoService.obtenerPedidosPorUsuario(usuario.getId()));
            }

            return "usuarios/perfil";
        }
        return "redirect:/dispositivos";
    }

    // RUTA 2: Acceso rápido al perfil del usuario logueado (Navbar)
    // Cambiamos el nombre del método y nos aseguramos de que la lógica redirija al ID
    @GetMapping("/mi-perfil") 
    public String irAMiPerfil(Authentication auth) {
        if (auth == null) return "redirect:/login";
        
        Usuario usuario = usuarioService.obtenerPorEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                
        return "redirect:/usuarios/perfil/" + usuario.getId();
    }
    
    @PostMapping("/crear")
    public String crearUsuario(@ModelAttribute Usuario usuario) {
        usuario.setTipo(TipoUsuario.USUARIO);
        // Aquí deberías codificar la contraseña cuando implementes registro
        usuarioService.crearUsuario(usuario);
        return "redirect:/usuarios";
    }
    
    // Este método ahora solo lo puede usar ADMIN (configurado en SecurityConfig)
    @GetMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable String id) {
        usuarioService.eliminarUsuario(id);
        return "redirect:/admin/panel";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditarPerfil(@PathVariable String id, Authentication authentication, Model model) {
        if (authentication == null) return "redirect:/login";

        Optional<Usuario> usuarioOpt = usuarioService.obtenerPorId(id);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            String email = authentication.getName();
            
            boolean esPropioPerfil = usuario.getEmail().equals(email);
            
            // Solo el dueño del perfil o un admin pueden ver el formulario
            if (esPropioPerfil) {
                model.addAttribute("usuario", usuario);
                return "usuarios/editar"; // Nueva vista que crearemos
            }
        }
        return "redirect:/usuarios";
    }

    @PostMapping("/editar/{id}")
    public String actualizarPerfil(@PathVariable String id, @ModelAttribute Usuario usuarioEditado, Authentication authentication) {
        if (authentication == null) return "redirect:/login";

        Optional<Usuario> usuarioOpt = usuarioService.obtenerPorId(id);
        if (usuarioOpt.isPresent()) {
            Usuario original = usuarioOpt.get();
            String email = authentication.getName();
            
            boolean esPropioPerfil = original.getEmail().equals(email);

            if (esPropioPerfil) {
                // Actualizamos solo los datos permitidos (nombre y teléfono)
                original.setNombre(usuarioEditado.getNombre());
                original.setTelefono(usuarioEditado.getTelefono());
                
                usuarioService.actualizarUsuario(original);
                return "redirect:/usuarios/perfil/" + id;
            }
        }
        return "redirect:/usuarios";
    }

    @GetMapping("/perfil")
    public String verPerfil(Authentication auth, Model model) {
        Usuario usuario = usuarioService.obtenerPorEmail(auth.getName()).get();
        model.addAttribute("usuario", usuario);
        model.addAttribute("pedidos", pedidoService.obtenerPedidosPorUsuario(usuario.getId()));
        return "usuarios/perfil";
    }
}
