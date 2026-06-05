package com.hotel.sistema.service;

import com.hotel.sistema.entity.Pago;
import com.hotel.sistema.repository.PagoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@SuppressWarnings("null")
public class PagoService {

    @Autowired private PagoRepository pagoRepository;
    @Autowired private EntityManager entityManager;

    public List<Pago> listarTodos() { return pagoRepository.findAll(); }

    public Optional<Pago> buscarPorId(Integer id) {
        if (id == null) return Optional.empty();
        return pagoRepository.findById(id);
    }

    public List<Pago> listarPorReservacion(Integer idReservacion) {
        return pagoRepository.findByReservacion_IdReservacion(idReservacion);
    }

    @Transactional
    public String procesarPagoSP(Integer idReservacion, Integer idMetodoPago, BigDecimal monto) {
        try {
            StoredProcedureQuery q = entityManager.createStoredProcedureQuery("sp_ProcesarPago");
            q.registerStoredProcedureParameter("idReservacion", Integer.class,    ParameterMode.IN);
            q.registerStoredProcedureParameter("idMetodoPago",  Integer.class,    ParameterMode.IN);
            q.registerStoredProcedureParameter("monto",         BigDecimal.class, ParameterMode.IN);
            q.registerStoredProcedureParameter("mensaje",       String.class,     ParameterMode.OUT);
            q.setParameter("idReservacion", idReservacion);
            q.setParameter("idMetodoPago",  idMetodoPago);
            q.setParameter("monto",         monto);
            q.execute();
            return (String) q.getOutputParameterValue("mensaje");
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    @Transactional
    public String cancelarReservacionSP(Integer idReservacion, String motivo, boolean reembolso) {
        try {
            StoredProcedureQuery q = entityManager.createStoredProcedureQuery("sp_CancelarReservacion");
            q.registerStoredProcedureParameter("idReservacion",    Integer.class, ParameterMode.IN);
            q.registerStoredProcedureParameter("motivo",           String.class,  ParameterMode.IN);
            q.registerStoredProcedureParameter("aplicarReembolso", Integer.class, ParameterMode.IN);
            q.registerStoredProcedureParameter("mensaje",          String.class,  ParameterMode.OUT);
            q.setParameter("idReservacion",    idReservacion);
            q.setParameter("motivo",           motivo);
            q.setParameter("aplicarReembolso", reembolso ? 1 : 0);
            q.execute();
            return (String) q.getOutputParameterValue("mensaje");
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }
}
