package com.hotel.sistema.controller;

import com.hotel.sistema.repository.*;
import com.hotel.sistema.service.ReservacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@RequestMapping("/reservaciones")
public class ReservacionController {

    @Autowired private ReservacionService   reservacionService;
    @Autowired private UsuarioRepository    usuarioRepository;
    @Autowired private HabitacionRepository habitacionRepository;
    @Autowired private OfertaRepository     ofertaRepository;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("reservaciones", reservacionService.listarTodas());
        return "reservaciones/lista";
    }

    @GetMapping("/nueva")
    public String nuevaForm(Model model) {
        model.addAttribute("usuarios",     usuarioRepository.findAll());
        model.addAttribute("habitaciones", habitacionRepository.findByEstado("disponible"));
        model.addAttribute("ofertas",      ofertaRepository.findByFechaFinGreaterThanEqual(LocalDate.now()));
        return "reservaciones/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam Integer idUsuario,
                          @RequestParam Integer idHabitacion,
                          @RequestParam String  fechaEntrada,
                          @RequestParam String  fechaSalida,
                          @RequestParam(required = false) Integer idOferta,
                          @RequestParam(required = false, defaultValue = "0") BigDecimal totalPago,
                          RedirectAttributes ra) {

        // Llamar al Stored Procedure principal
        String resultado = reservacionService.crearReservacionSP(
                idUsuario,
                idHabitacion,
                LocalDate.parse(fechaEntrada),
                LocalDate.parse(fechaSalida),
                idOferta
        );

        if (resultado != null && resultado.startsWith("OK")) {
            ra.addFlashAttribute("exito", resultado.replace("OK: ", ""));
        } else {
            ra.addFlashAttribute("error", resultado != null ? resultado.replace("ERROR: ", "") : "Error al crear reservación");
            return "redirect:/reservaciones/nueva";
        }
        return "redirect:/reservaciones";
    }

    @GetMapping("/cancelar/{id}")
    public String cancelar(@PathVariable Integer id, RedirectAttributes ra) {
        reservacionService.cancelar(id);
        ra.addFlashAttribute("exito", "Reservación cancelada.");
        return "redirect:/reservaciones";
    }

    @GetMapping("/detalle/{id}")
    public String detalle(@PathVariable Integer id, Model model) {
        model.addAttribute("reservacion",
                reservacionService.buscarPorId(id).orElse(null));
        return "reservaciones/detalle";
    }
}
