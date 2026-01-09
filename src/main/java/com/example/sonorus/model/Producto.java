package com.example.sonorus.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private double precio;
    private String imagen; // para mostrar en el index
    //private int stock;

    public Producto() {
    }

    public Producto(String nombre, double precio, String imagen/*, int stock*/) {
        this.nombre = nombre;
        this.precio = precio;
        this.imagen = imagen;
        //this.stock = stock;
    }

    // Un producto puede estar en muchos ítems de carrito
    @OneToMany(
        mappedBy = "producto", 
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY 
    )
    private List<ItemCarrito> itemsCarrito;
    

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    /* public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }*/
}
