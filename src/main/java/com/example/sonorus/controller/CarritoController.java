
package com.example.sonorus.controller;


import com.example.sonorus.servicio.CarritoServicio;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    private final CarritoServicio carritoServicio;

    public CarritoController(CarritoServicio carritoServicio) {
        this.carritoServicio = carritoServicio;
    }

    @GetMapping
    public String mostrarCarrito(Model model) {
        model.addAttribute("carrito", carritoServicio.obtenerCarrito());
        model.addAttribute("total", carritoServicio.calcularTotal());
        return "carrito"; // nueva vista
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id) {
        carritoServicio.eliminarProducto(id);
        return "redirect:/carrito";
    }
}