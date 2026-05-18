package com.ecommerce.electronicos.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Document(collection = "usuario")
public class Usuario {

    @Id
    private String id;

    @Field("nombre")
    private String nombre;

    @Field("email")
    private String email;

    @Field("password")
    private String password;
    
    @Field("telefono")
    private String telefono;

    @Field("tipo")
    private TipoUsuario tipo;

    @JsonIgnore
    @Field("dispositivosPublicados")
    private List<Dispositivo> dispositivosPublicados;

    @JsonIgnore
    @DBRef
    private List<Dispositivo> carrito = new ArrayList<>();
    
    public Usuario() {
        this.dispositivosPublicados = new ArrayList<>();
    }
    
    public Usuario(String id, String nombre, String email,String password, String telefono, TipoUsuario tipo,
        List<Dispositivo> dispositivosPublicados, List<Dispositivo> carrito) {
        this();
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.telefono = telefono;
        this.tipo = tipo;
        this.dispositivosPublicados = dispositivosPublicados;
        this.carrito = carrito;
    }
    
    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; } 
    public void setPassword(String password) { this.password = password; } 
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public TipoUsuario getTipo() { return tipo; }
    public void setTipo(TipoUsuario tipo) { this.tipo = tipo; }
    
    public List<Dispositivo> getDispositivosPublicados() { return dispositivosPublicados; }
    public void setDispositivosPublicados(List<Dispositivo> dispositivosPublicados) { 
        this.dispositivosPublicados = dispositivosPublicados; 
    }
    
    public boolean esAdministrador() {
        return this.tipo == TipoUsuario.ADMINISTRADOR;
    }

    public List<Dispositivo> getCarrito() { 
        return carrito; 
    }
    
    public void setCarrito(List<Dispositivo> carrito) { 
        this.carrito = carrito; 
    }
}