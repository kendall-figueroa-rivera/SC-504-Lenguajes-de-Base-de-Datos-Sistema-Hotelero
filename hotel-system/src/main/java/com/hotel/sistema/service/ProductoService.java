package com.hotel.sistema.service;

import com.hotel.sistema.entity.Producto;
import com.hotel.sistema.repository.ProductoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@SuppressWarnings("null")
public class ProductoService {

    @Autowired private ProductoRepository productoRepository;
    @Autowired private EntityManager entityManager;

    public List<Producto> listarTodos() { return productoRepository.findAll(); }

    public Optional<Producto> buscarPorId(Integer id) {
        if (id == null) return Optional.empty();
        return productoRepository.findById(id);
    }

    @Transactional
    public Producto guardar(Producto producto) { return productoRepository.save(producto); }

    @Transactional
    public void eliminar(Integer id) { if (id != null) productoRepository.deleteById(id); }

    @Transactional
    public String registrarVentaSP(Integer idUsuario, Integer idProducto, Integer cantidad) {
        try {
            StoredProcedureQuery q = entityManager.createStoredProcedureQuery("sp_RegistrarVenta");
            q.registerStoredProcedureParameter("idUsuario",  Integer.class, ParameterMode.IN);
            q.registerStoredProcedureParameter("idProducto", Integer.class, ParameterMode.IN);
            q.registerStoredProcedureParameter("cantidad",   Integer.class, ParameterMode.IN);
            q.registerStoredProcedureParameter("mensaje",    String.class,  ParameterMode.OUT);
            q.setParameter("idUsuario",  idUsuario);
            q.setParameter("idProducto", idProducto);
            q.setParameter("cantidad",   cantidad);
            q.execute();
            return (String) q.getOutputParameterValue("mensaje");
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }
}
