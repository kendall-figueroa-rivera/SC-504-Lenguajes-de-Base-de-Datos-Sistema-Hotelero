package com.hotel.sistema.repository;
import com.hotel.sistema.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {
    List<Pago> findByReservacion_IdReservacion(Integer idReservacion);
}
