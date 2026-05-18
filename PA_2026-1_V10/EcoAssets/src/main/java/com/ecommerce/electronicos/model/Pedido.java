package com.ecommerce.electronicos.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "pedido")
public class Pedido {
    @Id
    private String id;
    
    private LocalDateTime fecha;
    
    @DBRef
    private Usuario comprador;
    
    @DBRef
    private List<Dispositivo> productos;
    
    private double total;

    public Pedido() {
        this.fecha = LocalDateTime.now();
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public Usuario getComprador() { return comprador; }
    public void setComprador(Usuario comprador) { this.comprador = comprador; }
    public List<Dispositivo> getProductos() { return productos; }
    public void setProductos(List<Dispositivo> productos) { this.productos = productos; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}
