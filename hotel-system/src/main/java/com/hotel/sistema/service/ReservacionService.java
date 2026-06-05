package com.hotel.sistema.service;

import com.hotel.sistema.entity.*;
import com.hotel.sistema.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    // Llamada a Stored Procedure de SQL Server
    // Ajustar nombre y parámetros al SP real creado en SSMS
    // -------------------------------------------------------
    @Transactional
    public String crearReservacionSP(Integer idUsuario, Integer idHabitacion,
                                      LocalDate fechaEntrada, LocalDate fechaSalida,
                                      Integer idOferta) {
        try {
            StoredProcedureQuery query = entityManager
                    .createStoredProcedureQuery("sp_CrearReservacion");

            query.registerStoredProcedureParameter("idUsuario",    Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("idHabitacion", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("fechaEntrada", String.class,  ParameterMode.IN);
            query.registerStoredProcedureParameter("fechaSalida",  String.class,  ParameterMode.IN);
            query.registerStoredProcedureParameter("idOferta",     Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("mensaje",      String.class,  ParameterMode.OUT);

            query.setParameter("idUsuario",    idUsuario);
            query.setParameter("idHabitacion", idHabitacion);
            query.setParameter("fechaEntrada", fechaEntrada.toString());
            query.setParameter("fechaSalida",  fechaSalida.toString());
            query.setParameter("idOferta",     idOferta);

            query.execute();
            return (String) query.getOutputParameterValue("mensaje");
        } catch (Exception e) {
            return "Error al ejecutar procedimiento: " + e.getMessage();
        }
    }
}
