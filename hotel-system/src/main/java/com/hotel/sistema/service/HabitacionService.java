package com.hotel.sistema.service;

import com.hotel.sistema.entity.Habitacion;
import com.hotel.sistema.repository.HabitacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@SuppressWarnings("null")
public class HabitacionService {

    @Autowired
    private HabitacionRepository habitacionRepository;

    public List<Habitacion> listarTodas() {
        return habitacionRepository.findAll();
    }

    public List<Habitacion> listarDisponibles() {
        return habitacionRepository.findByEstado("disponible");
    }

    public List<Habitacion> buscarDisponiblesPorFechas(LocalDate entrada, LocalDate salida) {
        return habitacionRepository.findHabitacionesDisponibles(entrada, salida);
    }

    public Optional<Habitacion> buscarPorId(Integer id) {
        if (id == null) return Optional.empty();
        return habitacionRepository.findById(id);
    }

    @Transactional
    public Habitacion guardar(Habitacion habitacion) {
        return habitacionRepository.save(habitacion);
    }

    @Transactional
    public void eliminar(Integer id) {
        if (id != null) habitacionRepository.deleteById(id);
    }
}
