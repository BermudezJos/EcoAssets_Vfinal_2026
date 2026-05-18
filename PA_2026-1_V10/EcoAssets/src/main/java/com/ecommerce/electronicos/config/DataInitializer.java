package com.ecommerce.electronicos.config;

import com.ecommerce.electronicos.model.Dispositivo;
import com.ecommerce.electronicos.model.TipoUsuario;
import com.ecommerce.electronicos.model.Usuario;
import com.ecommerce.electronicos.service.DispositivoService;
import com.ecommerce.electronicos.service.UsuarioService;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {
    
    private final UsuarioService usuarioService;
    private final DispositivoService dispositivoService;
    private final PasswordEncoder passwordEncoder;
 
    public DataInitializer(UsuarioService usuarioService,
                           DispositivoService dispositivoService,
                           PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.dispositivoService = dispositivoService;
        this.passwordEncoder = passwordEncoder;
    }
 
    @PostConstruct
    public void init() {
        // ✅ Si ya hay datos en MongoDB, no insertar nada y evita duplicados
        if (!usuarioService.obtenerTodos().isEmpty()) {
            return;
        }
        // ✅ Usar el constructor sin argumentos + setters (no pasar null como id)
        Usuario usuario1 = crearUsuario("Carlos Rodríguez", "carlos@email.com", "123456", "555-0101", TipoUsuario.USUARIO);
        Usuario usuario2 = crearUsuario("María González",   "maria@email.com",  "123456", "555-0102", TipoUsuario.USUARIO);
        Usuario usuario3 = crearUsuario("Juan Pérez",       "juan@email.com",   "123456", "555-0103", TipoUsuario.USUARIO);
        Usuario usuario4 = crearUsuario("Ana Martínez",     "ana@email.com",    "123456", "555-0104", TipoUsuario.USUARIO);
 
        crearUsuario("Admin Principal",   "admin@electronicos.com",      "admin123", "555-0001", TipoUsuario.ADMINISTRADOR);
        crearUsuario("Supervisor Ventas", "supervisor@electronicos.com", "admin123", "555-0002", TipoUsuario.ADMINISTRADOR);
 
        crearDispositivo("iPhone 15 Pro Max", "Apple", "A2849",
            "Smartphone de última generación con chip A17 Pro, cámara de 48MP y titanio.",
            1299.99, "Smartphones", "Usado",
            "https://store.storeimages.cdn-apple.com/4982/as-images.apple.com/is/iphone-15-pro-finish-select-202309-6-7inch-bluetitanium?wid=5120&hei=2880&fmt=webp&qlt=70&.v=1692895703312",
            usuario1);
 
        crearDispositivo("Samsung Galaxy S24 Ultra", "Samsung", "SM-S928B",
            "Android flagship con S Pen integrado, cámara de 200MP y pantalla AMOLED 120Hz.",
            1199.99, "Smartphones", "Usado",
            "https://images.samsung.com/is/image/samsung/p6pim/mx/2401/gallery/mx-galaxy-s24-s928-sm-s928bzkhlmx-539573293?$650_519_PNG$",
            usuario2);
 
        crearDispositivo("MacBook Pro 16 M3 Max", "Apple", "MRW23",
            "Laptop profesional con chip M3 Max, 36GB RAM y 1TB SSD.",
            3499.99, "Laptops", "Usado",
            "https://store.storeimages.cdn-apple.com/4982/as-images.apple.com/is/mbp16-spaceblack-select-202310?wid=904&hei=840&fmt=jpeg&qlt=90&.v=1697311054290",
            usuario1);
 
        crearDispositivo("Dell XPS 15", "Dell", "9530",
            "Laptop premium con Intel Core i9, RTX 4070 y pantalla OLED 3.5K.",
            2499.99, "Laptops", "Usado",
            "https://i.dell.com/is/image/DellContent/content/dam/ss2/product-images/dell-client-products/notebooks/xps-notebooks/xps-15-9530/media-gallery/touch-black/notebook-xps-15-9530-t-black-gallery-1.psd?fmt=png-alpha&op_usm=1.75,0.3,2,0&resMode=sharp2&size=840,630&hei=630&wid=840",
            usuario3);
 
        crearDispositivo("PlayStation 5", "Sony", "CFI-1215",
            "Consola de nueva generación con SSD ultrarrápido y ray tracing.",
            499.99, "Consolas", "Reacondicionado",
            "https://gmedia.playstation.com/is/image/SIEPDC/ps5-product-thumbnail-01-en-14sep21?$facebook$",
            usuario2);
 
        crearDispositivo("Xbox Series X", "Microsoft", "1TB",
            "Consola más potente de Xbox con 12 TFLOPS y 4K a 120fps.",
            499.99, "Consolas", "Reacondicionado",
            "https://assets.xboxservices.com/assets/0b/28/0b2854b9-a7e5-47b2-9535-7a7bfa9c1e2f.png?n=Xbox-Series-X_Image-0_1083x1222.png",
            usuario4);
    }
 
    // ✅ Crea usuario sin pasar null como id — MongoDB genera el ObjectId solo
    private Usuario crearUsuario(String nombre, String email, String password,
                                  String telefono, TipoUsuario tipo) {
        Usuario u = new Usuario();   // constructor vacío, sin pasar null
        u.setNombre(nombre);
        u.setEmail(email);
        u.setPassword(passwordEncoder.encode(password));
        u.setTelefono(telefono);
        u.setTipo(tipo);
        return usuarioService.crearUsuario(u);
    }
 
    private void crearDispositivo(String nombre, String marca, String modelo, String descripcion,
                                   double precio, String categoria, String estado,
                                   String imagenUrl, Usuario vendedor) {
        Dispositivo d = new Dispositivo();
        d.setNombre(nombre);
        d.setMarca(marca);
        d.setModelo(modelo);
        d.setDescripcion(descripcion);
        d.setPrecio(precio);
        d.setCategoria(categoria);
        d.setEstado(estado);
        d.setImagenUrl(imagenUrl);
        d.setVendedor(vendedor);
        dispositivoService.crearDispositivo(d);
    }
}