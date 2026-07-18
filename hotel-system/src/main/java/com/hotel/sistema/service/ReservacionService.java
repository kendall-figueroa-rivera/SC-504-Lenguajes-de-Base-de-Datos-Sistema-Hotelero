package com.hotel.sistema.service;

import com.hotel.sistema.entity.*;
import com.hotel.sistema.repository.*;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.Types;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@SuppressWarnings("null")
public class ReservacionService {

    @Autowired
    private ReservacionRepository reservacionRepository;

    @Autowired
    private HabitacionRepository habitacionRepository;

    @Autowired
    private EntityManager entityManager;

    public List<Reservacion> listarTodas() {
        return reservacionRepository.findAll();
    }

    public Optional<Reservacion> buscarPorId(Integer id) {
        if (id == null) return Optional.empty();
        return reservacionRepository.findById(id);
    }

    public List<Reservacion> listarPorUsuario(Integer idUsuario) {
        return reservacionRepository.findByUsuario_IdUsuario(idUsuario);
    }

    public List<Habitacion> buscarHabitacionesDisponibles(LocalDate entrada, LocalDate salida) {
        return habitacionRepository.findHabitacionesDisponibles(entrada, salida);
    }

    @Transactional
    public Reservacion guardar(Reservacion reservacion) {
        return reservacionRepository.save(reservacion);
    }

    @Transactional
    public void cancelar(Integer idReservacion) {
        if (idReservacion == null) return;
        reservacionRepository.findById(idReservacion).ifPresent(r -> {
            r.setEstado("cancelada");
            reservacionRepository.save(r);
        });
    }

    // -------------------------------------------------------
    // Llamada al paquete pkg_reservaciones.crear (Oracle)
    // -------------------------------------------------------
    @Transactional
    public String crearReservacionSP(Integer idUsuario, Integer idHabitacion,
                                      LocalDate fechaEntrada, LocalDate fechaSalida,
                                      Integer idOferta) {
        final String[] resultado = new String[1];

        try {
            Session session = entityManager.unwrap(Session.class);

            session.doWork(connection -> {
                String sql = "{call pkg_reservaciones.crear(?,?,?,?,?,?)}";

                try (CallableStatement stmt = connection.prepareCall(sql)) {
                    stmt.setInt(1, idUsuario);
                    stmt.setInt(2, idHabitacion);
                    stmt.setDate(3, Date.valueOf(fechaEntrada));
                    stmt.setDate(4, Date.valueOf(fechaSalida));

                    if (idOferta != null) {
                        stmt.setInt(5, idOferta);
                    } else {
                        stmt.setNull(5, Types.INTEGER);
                    }

                    stmt.registerOutParameter(6, Types.VARCHAR);
                    stmt.execute();

                    resultado[0] = stmt.getString(6);
                }
            });

        } catch (Exception e) {
            resultado[0] = "ERROR: " + e.getMessage();
        }

        return resultado[0];
    }
}