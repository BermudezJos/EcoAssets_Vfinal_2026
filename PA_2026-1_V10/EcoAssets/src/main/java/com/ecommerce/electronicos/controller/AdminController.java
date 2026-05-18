package com.ecommerce.electronicos.controller;

import com.ecommerce.electronicos.model.TipoUsuario;
import com.ecommerce.electronicos.model.Usuario;
import com.ecommerce.electronicos.service.DispositivoService;
import com.ecommerce.electronicos.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    private final UsuarioService usuarioService;
    private final DispositivoService dispositivoService;
    
    
    public AdminController(UsuarioService usuarioService, 
                          DispositivoService dispositivoService) {
        this.usuarioService = usuarioService;
        this.dispositivoService = dispositivoService;
        
    }
    
    @GetMapping("/panel")
    public String panelAdministracion(Model model) {
        model.addAttribute("usuarios", usuarioService.obtenerTodos());
        model.addAttribute("dispositivos", dispositivoService.obtenerTodos());
        model.addAttribute("totalUsuarios", usuarioService.obtenerTodos().size());
        model.addAttribute("totalDispositivos", dispositivoService.obtenerTodos().size());
        model.addAttribute("totalNormales", usuarioService.obtenerUsuariosNormales().size());
        model.addAttribute("totalAdmins", usuarioService.obtenerAdministradores().size());
        return "admin/panel";
    }
    
    // 1. Método para mostrar el formulario de registro adaptado para Admin
    @GetMapping("/usuarios/nuevo")
    public String mostrarFormularioAdmin(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("esAdmin", true); // Bandera para cambiar el HTML
        model.addAttribute("rutaDestino", "/admin/usuarios/crear"); // Ruta a donde se enviará el formulario
        return "registro"; // Reutilizamos tu plantilla registro.html
    }

    // 2. Método para recibir los datos y guardarlos
    @PostMapping("/usuarios/crear")
    public String crearAdministrador(@ModelAttribute Usuario usuario, RedirectAttributes redirectAttributes) {
        try {
            // Forzamos que el tipo sea Administrador por seguridad, sin importar lo que venga del formulario
            usuario.setTipo(TipoUsuario.ADMINISTRADOR);
            
            // Usamos tu servicio para encriptar y guardar
            usuarioService.crearUsuario(usuario); 
            
            redirectAttributes.addFlashAttribute("mensaje", "Administrador creado exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear el administrador: " + e.getMessage());
        }
        
        return "redirect:/admin/panel";
    }    
    @GetMapping("/dispositivos/eliminar/{id}")
    public String eliminarDispositivo(@PathVariable String id) {
        dispositivoService.eliminarDispositivo(id);
        return "redirect:/admin/panel";
    }
}