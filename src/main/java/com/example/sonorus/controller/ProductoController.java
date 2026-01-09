
package com.example.sonorus.controller;

import com.example.sonorus.repository.ProductoRepository;
import com.example.sonorus.servicio.CarritoServicio;
import com.example.sonorus.model.Producto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CarritoServicio carritoServicio;

    // Mostrar productos en la página principal
    @GetMapping("/")
    public String mostrarProductos(Model model) {
        model.addAttribute("productos", productoRepository.findAll()); // esto muestra los productos en el index, los prodcutos de la bd
        model.addAttribute("carrito", carritoServicio.obtenerCarrito()); //esto no se usa para el index ya 
        model.addAttribute("total", carritoServicio.calcularTotal()); // esto tampoco se usa, ya que no tenemos modal
        return "index";
    }

    // Agregar producto al carrito
    @PostMapping("/carrito/agregar/{id}")
    public String agregarAlCarrito(@PathVariable Long id) {
        Producto producto = productoRepository.findById(id).orElse(null);
        if (producto != null) {
            carritoServicio.agregarProducto(producto);
        }
        return "redirect:/";
    }
}