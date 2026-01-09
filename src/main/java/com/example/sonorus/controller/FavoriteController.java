package com.example.sonorus.controller;

import com.example.sonorus.model.Favorite;
import com.example.sonorus.servicio.FavoriteServicio;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/favoritos")
public class FavoriteController {

    private final FavoriteServicio favoriteServicio;

    public FavoriteController(FavoriteServicio favoriteServicio) {
        this.favoriteServicio = favoriteServicio;
    }

    @GetMapping
    public String listarFavoritos(Model model) {
        model.addAttribute("favoritos", favoriteServicio.obtenerFavoritos());
        return "favoritos"; // archivo favoritos.html
    }

    @PostMapping("/agregar")
    public String agregarFavorito(@RequestParam String nombre, @RequestParam String artista) {
        favoriteServicio.agregarFavorito(new Favorite(nombre, artista));
        return "redirect:/";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarFavorito(@PathVariable Long id) {
        favoriteServicio.eliminarFavoritoPorId(id);
        return "redirect:/favoritos";
    }

}