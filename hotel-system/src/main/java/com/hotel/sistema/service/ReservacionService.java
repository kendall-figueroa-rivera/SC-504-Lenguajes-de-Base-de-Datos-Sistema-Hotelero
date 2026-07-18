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

    @Autowired private ReservacionRepository reservacionRepository;
    @Autowired private HabitacionRepository  habitacionRepository;
    @Autowired private EntityManager         entityManager;

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

    // Buscar proxima disponibilidad de una habitacion
    public String buscarProximaDisponibilidad(Integer idHabitacion, LocalDate desde) {
        try {
            // Buscar la reservacion activa mas proxima que bloquea esta habitacion
            List<Reservacion> reservaciones = reservacionRepository
                    .findByHabitacion_IdHabitacion(idHabitacion);

            LocalDate proximaDisponible = null;
            for (Reservacion r : reservaciones) {
                if (!r.getEstado().equals("cancelada")
                        && r.getFechaSalida().isAfter(desde)) {
                    if (proximaDisponible == null
                            || r.getFechaSalida().isAfter(proximaDisponible)) {
                        proximaDisponible = r.getFechaSalida();
                    }
                }
            }

            if (proximaDisponible != null) {
                return "La habitacion no esta disponible en esa fecha. " +
                       "Proxima disponibilidad: " + proximaDisponible.toString();
            }
            return "La habitacion no esta disponible en esas fechas.";
        } catch (Exception e) {
            return "La habitacion no esta disponible en esas fechas.";
        }
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

<<<<<<< HEAD
    // Llamada al paquete Oracle pkg_reservaciones.crear
=======
    // -------------------------------------------------------
    // Llamada al paquete pkg_reservaciones.crear (Oracle)
    // -------------------------------------------------------
>>>>>>> origin/feature/persona3_sp_ReporteVentasProductos
    @Transactional
    public String crearReservacionSP(Integer idUsuario, Integer idHabitacion,
                                      LocalDate fechaEntrada, LocalDate fechaSalida,
                                      Integer idOferta) {
        final String[] resultado = new String[1];

        try {
<<<<<<< HEAD
            StoredProcedureQuery q = entityManager
                    .createStoredProcedureQuery("pkg_reservaciones.crear");

            q.registerStoredProcedureParameter("p_idUsuario",    Integer.class,   ParameterMode.IN);
            q.registerStoredProcedureParameter("p_idHabitacion", Integer.class,   ParameterMode.IN);
            q.registerStoredProcedureParameter("p_fechaEntrada", java.sql.Date.class, ParameterMode.IN);
            q.registerStoredProcedureParameter("p_fechaSalida",  java.sql.Date.class, ParameterMode.IN);
            q.registerStoredProcedureParameter("p_idOferta",     Integer.class,   ParameterMode.IN);
            q.registerStoredProcedureParameter("p_mensaje",      String.class,    ParameterMode.OUT);

            q.setParameter("p_idUsuario",    idUsuario);
            q.setParameter("p_idHabitacion", idHabitacion);
            q.setParameter("p_fechaEntrada", java.sql.Date.valueOf(fechaEntrada));
            q.setParameter("p_fechaSalida",  java.sql.Date.valueOf(fechaSalida));
            q.setParameter("p_idOferta",     idOferta);

            q.execute();
            return (String) q.getOutputParameterValue("p_mensaje");
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
=======
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
>>>>>>> origin/feature/persona3_sp_ReporteVentasProductos
        }

        return resultado[0];
    }
}