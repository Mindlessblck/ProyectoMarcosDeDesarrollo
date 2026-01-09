
package com.example.sonorus.repository;

import com.example.sonorus.model.ItemCarrito;
import com.example.sonorus.model.Producto;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ItemCarritoRepository extends JpaRepository<ItemCarrito, Long>{
        Optional<ItemCarrito> findByProducto(Producto producto);

}
