package com.example.sonorus.repository;

import com.example.sonorus.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoritosRepository extends JpaRepository<Favorite, Long> {
    void deleteByNombreIgnoreCase(String nombre);
}
