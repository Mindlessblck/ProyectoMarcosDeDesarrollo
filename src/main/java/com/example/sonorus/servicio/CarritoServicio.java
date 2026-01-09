package com.example.sonorus.servicio;

import com.example.sonorus.model.ItemCarrito;
import com.example.sonorus.model.Producto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CarritoServicio {

    private List<ItemCarrito> carrito = new ArrayList<>();

    // Agrega un producto al carrito (si ya existe, aumenta la cantidad)
    public void agregarProducto(Producto producto) {
        for (ItemCarrito item : carrito) {
            if (item.getProducto().getId().equals(producto.getId())) {
                item.setCantidad(item.getCantidad() + 1);
                return;
            }
        }
        carrito.add(new ItemCarrito(producto, 1));
    }

    // Elimina un producto por ID
    public void eliminarProducto(Long id) {
        carrito.removeIf(item -> item.getProducto().getId().equals(id));
    }

    // Devuelve la lista de productos en el carrito
    public List<ItemCarrito> obtenerCarrito() {
        return carrito;
    }

    // Calcula el total
    public double calcularTotal() {
        return carrito.stream().mapToDouble(ItemCarrito::getSubtotal).sum();
    }
    
    public void vaciarCarrito() {
        carrito.clear();
    }

 
}