/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.sonorus.controller;

import com.example.sonorus.model.Orden;
import com.example.sonorus.model.Usuario;
import com.example.sonorus.servicio.OrdenServicio;
import com.example.sonorus.servicio.UsuarioServicio;
import com.example.sonorus.util.PdfGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    private final OrdenServicio ordenServicio;
    private final UsuarioServicio usuarioServicio;

    public CheckoutController(OrdenServicio ordenServicio, UsuarioServicio usuarioServicio) {
        this.ordenServicio = ordenServicio;
        this.usuarioServicio = usuarioServicio;
    }

    @GetMapping
    public String mostrarCheckout(Model model) {
        // puedes añadir datos del usuario si están en sesión
        model.addAttribute("ordenForm", new CheckoutForm());
        return "checkout";
    }

    @PostMapping("/confirmar")
    public String confirmarCompra(@ModelAttribute CheckoutForm form, Model model) {

        // 1. Recuperar email del usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();  

        // 2. Buscar usuario en la BD (tu Usuario ya es entidad JPA)
        Usuario usuario = usuarioServicio.buscarPorEmail(email);

        Long usuarioId = usuario.getId();

        // Enmascarar tarjeta
        String tarjeta = form.getTarjeta();
        String tarjetaMask = "**** **** **** " + tarjeta.substring(tarjeta.length() - 4);

        // 3. Crear la orden
        Orden orden = ordenServicio.crearOrden(
                usuarioId,
                form.getDireccion(),
                form.getCiudad(),
                tarjetaMask
        );

        return "redirect:/checkout/success/" + orden.getId();
    }


    @GetMapping("/success/{ordenId}")
    public String success(@PathVariable Long ordenId, Model model) {
        model.addAttribute("ordenId", ordenId);
        return "checkout_success";
    }

    // Endpoint que genera y devuelve PDF (boleta)
    @GetMapping("/boleta/{ordenId}")
    public void descargarBoleta(@PathVariable Long ordenId, HttpServletResponse response) throws IOException {
        // delega la generación PDF a un servicio util (ver ejemplo abajo)
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=boleta_" + ordenId + ".pdf");

        PdfGenerator.generarBoletaPdf(ordenId, response.getOutputStream());
    }

    // Clase interna para el formulario (puedes mover a un archivo)
    public static class CheckoutForm {
        private String nombre;
        private String direccion;
        private String ciudad;
        private String tarjeta;
        private String expiracion;
        private String cvv;
        // getters y setters

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getDireccion() {
            return direccion;
        }

        public void setDireccion(String direccion) {
            this.direccion = direccion;
        }

        public String getCiudad() {
            return ciudad;
        }

        public void setCiudad(String ciudad) {
            this.ciudad = ciudad;
        }

        public String getTarjeta() {
            return tarjeta;
        }

        public void setTarjeta(String tarjeta) {
            this.tarjeta = tarjeta;
        }

        public String getExpiracion() {
            return expiracion;
        }

        public void setExpiracion(String expiracion) {
            this.expiracion = expiracion;
        }

        public String getCvv() {
            return cvv;
        }

        public void setCvv(String cvv) {
            this.cvv = cvv;
        }
        
        
    }
}
