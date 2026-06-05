package com.hotel.sistema.controller;

import com.hotel.sistema.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @Autowired private UsuarioRepository     usuarioRepository;
    @Autowired private HabitacionRepository  habitacionRepository;
    @Autowired private ReservacionRepository reservacionRepository;
    @Autowired private ProductoRepository    productoRepository;
    @Autowired private PagoRepository        pagoRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalUsuarios",        usuarioRepository.count());
        model.addAttribute("totalHabitaciones",    habitacionRepository.count());
        model.addAttribute("habitacionesDisp",     habitacionRepository.findByEstado("disponible").size());
        model.addAttribute("habitacionesOcupadas", habitacionRepository.findByEstado("ocupada").size());
        model.addAttribute("totalReservaciones",   reservacionRepository.count());
        model.addAttribute("reservacionesActivas", reservacionRepository.findByEstado("confirmada").size());
        model.addAttribute("totalProductos",       productoRepository.count());
        model.addAttribute("totalPagos",           pagoRepository.count());
        return "dashboard/index";
    }

    @GetMapping("/")
    public String root() { return "redirect:/dashboard"; }
}
