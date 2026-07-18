package com.hotel.sistema.controller;

import com.hotel.sistema.entity.Reservacion;
import com.hotel.sistema.repository.*;
import com.hotel.sistema.service.ReservacionService;
import com.hotel.sistema.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Controller
@RequestMapping("/reservaciones")
public class ReservacionController {

    @Autowired private ReservacionService   reservacionService;
    @Autowired private UsuarioRepository    usuarioRepository;
    @Autowired private HabitacionRepository habitacionRepository;
    @Autowired private OfertaRepository     ofertaRepository;
    @Autowired private UsuarioService       usuarioService;

    @GetMapping
    public String listar(Model model, Authentication auth) {
        boolean esCliente = auth.getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_CLIENTE"));

        if (esCliente) {
            // Cliente solo ve SUS reservaciones
            usuarioService.buscarPorUsername(auth.getName()).ifPresent(u -> {
                List<Reservacion> misReservaciones =
                        reservacionService.listarPorUsuario(u.getIdUsuario());
                model.addAttribute("reservaciones", misReservaciones);
                model.addAttribute("esCliente", true);
            });
        } else {
            // Admin y recepcionista ven todas
            model.addAttribute("reservaciones", reservacionService.listarTodas());
            model.addAttribute("esCliente", false);
        }
        return "reservaciones/lista";
    }

    @GetMapping("/nueva")
    public String nuevaForm(Model model, Authentication auth) {
        boolean esCliente = auth.getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_CLIENTE"));

        if (esCliente) {
            // Cliente se reserva a sí mismo — no necesita elegir usuario
            usuarioService.buscarPorUsername(auth.getName()).ifPresent(u ->
                    model.addAttribute("usuarioActual", u));
        } else {
            model.addAttribute("usuarios", usuarioRepository.findAll());
        }

        model.addAttribute("habitaciones",
                habitacionRepository.findByEstado("disponible"));
        model.addAttribute("ofertas",
                ofertaRepository.findByFechaFinGreaterThanEqual(LocalDate.now()));
        model.addAttribute("esCliente", esCliente);
        return "reservaciones/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam(required = false) Integer idUsuario,
                          @RequestParam Integer idHabitacion,
                          @RequestParam String  fechaEntrada,
                          @RequestParam String  fechaSalida,
                          @RequestParam(required = false) Integer idOferta,
                          @RequestParam(required = false, defaultValue = "0") BigDecimal totalPago,
                          Authentication auth,
                          RedirectAttributes ra) {

        boolean esCliente = auth.getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_CLIENTE"));

        // Si es cliente, usar su propio ID
        if (esCliente) {
            idUsuario = usuarioService.buscarPorUsername(auth.getName())
                    .map(u -> u.getIdUsuario()).orElse(null);
        }

        if (idUsuario == null) {
            ra.addFlashAttribute("error", "No se pudo identificar el usuario.");
            return "redirect:/reservaciones/nueva";
        }

        LocalDate entrada = LocalDate.parse(fechaEntrada);
        LocalDate salida  = LocalDate.parse(fechaSalida);

        // Verificar disponibilidad antes de llamar al SP
        List<?> disponibles = reservacionService.buscarHabitacionesDisponibles(entrada, salida);
        boolean disponible = disponibles.stream()
                .anyMatch(h -> {
                    if (h instanceof com.hotel.sistema.entity.Habitacion hab) {
                        return hab.getIdHabitacion().equals(idHabitacion);
                    }
                    return false;
                });

        if (!disponible) {
            // Buscar hasta cuando esta ocupada
            String mensajeDisp = reservacionService.buscarProximaDisponibilidad(idHabitacion, entrada);
            ra.addFlashAttribute("error", mensajeDisp);
            return "redirect:/reservaciones/nueva";
        }

        String resultado = reservacionService.crearReservacionSP(
                idUsuario, idHabitacion, entrada, salida, idOferta);

        if (resultado != null && resultado.startsWith("OK")) {
            ra.addFlashAttribute("exito", resultado.replace("OK: ", ""));
        } else {
            ra.addFlashAttribute("error", resultado != null
                    ? resultado.replace("ERROR: ", "")
                    : "Error al crear la reservacion");
            return "redirect:/reservaciones/nueva";
        }
        return "redirect:/reservaciones";
    }

    @GetMapping("/cancelar/{id}")
    public String cancelar(@PathVariable Integer id, RedirectAttributes ra) {
        reservacionService.cancelar(id);
        ra.addFlashAttribute("exito", "Reservacion cancelada.");
        return "redirect:/reservaciones";
    }

    @GetMapping("/detalle/{id}")
    public String detalle(@PathVariable Integer id, Model model, Authentication auth) {
        boolean esCliente = auth.getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_CLIENTE"));

        reservacionService.buscarPorId(id).ifPresent(r -> {
            // Si es cliente verificar que sea SU reservacion
            if (esCliente) {
                usuarioService.buscarPorUsername(auth.getName()).ifPresent(u -> {
                    if (r.getUsuario().getIdUsuario().equals(u.getIdUsuario())) {
                        model.addAttribute("reservacion", r);
                        long noches = ChronoUnit.DAYS.between(r.getFechaEntrada(), r.getFechaSalida());
                        model.addAttribute("noches", noches);
                    }
                });
            } else {
                model.addAttribute("reservacion", r);
                long noches = ChronoUnit.DAYS.between(r.getFechaEntrada(), r.getFechaSalida());
                model.addAttribute("noches", noches);
            }
        });
        model.addAttribute("esCliente", esCliente);
        return "reservaciones/detalle";
    }
}
