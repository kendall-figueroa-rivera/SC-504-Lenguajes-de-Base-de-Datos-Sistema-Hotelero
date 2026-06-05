package com.hotel.sistema.service;

import com.hotel.sistema.entity.Oferta;
import com.hotel.sistema.repository.OfertaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@SuppressWarnings("null")
public class OfertaService {

    @Autowired private OfertaRepository ofertaRepository;

    public List<Oferta> listarTodas() { return ofertaRepository.findAll(); }

    public List<Oferta> listarVigentes() {
        return ofertaRepository.findByFechaFinGreaterThanEqual(LocalDate.now());
    }

    public Optional<Oferta> buscarPorId(Integer id) {
        if (id == null) return Optional.empty();
        return ofertaRepository.findById(id);
    }

    @Transactional
    public Oferta guardar(Oferta oferta) { return ofertaRepository.save(oferta); }

    @Transactional
    public void eliminar(Integer id) { if (id != null) ofertaRepository.deleteById(id); }
}
