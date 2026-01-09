/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.sonorus.servicio;

import com.example.sonorus.model.Orden;
import com.example.sonorus.model.OrdenDetalle;
import com.example.sonorus.model.ItemCarrito;
import com.example.sonorus.model.Producto;
import com.example.sonorus.repository.OrdenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrdenServicio {

    private final OrdenRepository ordenRepository;
    private final CarritoServicio carritoServicio;

    public OrdenServicio(OrdenRepository ordenRepository, CarritoServicio carritoServicio) {
        this.ordenRepository = ordenRepository;
        this.carritoServicio = carritoServicio;
    }

    @Transactional
    public Orden crearOrden(Long usuarioId, String direccion, String ciudad, String tarjetaMask) {
        List<ItemCarrito> carrito = carritoServicio.obtenerCarrito();

        Orden orden = new Orden();
        orden.setUsuarioId(usuarioId);
        orden.setFecha(LocalDateTime.now());
        orden.setDireccion(direccion);
        orden.setCiudad(ciudad);
        orden.setTarjetaEnmascarada(tarjetaMask);
        orden.setNumeroBoleta("B-" + UUID.randomUUID().toString().substring(0,8).toUpperCase());

        double total = carrito.stream().mapToDouble(ItemCarrito::getSubtotal).sum();
        orden.setTotal(total);

        List<OrdenDetalle> detalles = carrito.stream().map(item -> {
            OrdenDetalle d = new OrdenDetalle();
            d.setOrden(orden);
            Producto p = item.getProducto();
            d.setProductoId(p.getId());
            d.setNombreProducto(p.getNombre());
            d.setCantidad(item.getCantidad());
            d.setPrecioUnitario(p.getPrecio());
            d.setSubtotal(item.getSubtotal());
            return d;
        }).collect(Collectors.toList());

        orden.setDetalles(detalles);

        Orden guardada = ordenRepository.save(orden);

        // vaciar carrito después de guardar orden
        carritoServicio.vaciarCarrito();

        return guardada;
    }
}