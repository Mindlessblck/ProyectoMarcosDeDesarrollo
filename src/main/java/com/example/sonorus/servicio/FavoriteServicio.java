package com.example.sonorus.servicio;

import com.example.sonorus.model.Favorite;
import com.example.sonorus.repository.FavoritosRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FavoriteServicio {

    private final FavoritosRepository favoritosRepository;

    public FavoriteServicio(FavoritosRepository favoritosRepository) {
        this.favoritosRepository = favoritosRepository;
    }

    public void agregarFavorito(Favorite fav) {
        favoritosRepository.save(fav);
    }

    public List<Favorite> obtenerFavoritos() {
        return favoritosRepository.findAll();
    }

    public void eliminarFavoritoPorId(Long id) {
        favoritosRepository.deleteById(id);
    }

}
