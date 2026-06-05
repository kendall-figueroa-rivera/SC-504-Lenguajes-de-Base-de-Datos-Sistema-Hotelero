package com.hotel.sistema.controller;

import com.hotel.sistema.repository.MetodoPagoRepository;
import com.hotel.sistema.repository.ReservacionRepository;
import com.hotel.sistema.service.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;

@Controller
@RequestMapping("/pagos")
public class PagoController {

    @Autowired private PagoService pagoService;
    @Autowired private ReservacionRepository reservacionRepository;
    @Autowired private MetodoPagoRepository metodoPagoRepository;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("pagos", pagoService.listarTodos());
        return "pagos/lista";
    }

    @GetMapping("/nuevo")
    public String nuevoForm(Model model) {
        model.addAttribute("reservaciones", reservacionRepository.findByEstado("confirmada"));
        model.addAttribute("metodos",       metodoPagoRepository.findAll());
        return "pagos/formulario";
    }

    @PostMapping("/procesar")
    public String procesar(@RequestParam Integer idReservacion,
                           @RequestParam Integer idMetodoPago,
                           @RequestParam BigDecimal monto,
                           RedirectAttributes ra) {
        String resultado = pagoService.procesarPagoSP(idReservacion, idMetodoPago, monto);
        if (resultado.startsWith("OK")) {
            ra.addFlashAttribute("exito", resultado.replace("OK: ", ""));
        } else {
            ra.addFlashAttribute("error", resultado.replace("ERROR: ", ""));
            return "redirect:/pagos/nuevo";
        }
        return "redirect:/pagos";
    }

    @GetMapping("/cancelar/{id}")
    public String cancelarForm(@PathVariable Integer id, Model model) {
        reservacionRepository.findById(id).ifPresent(r -> model.addAttribute("reservacion", r));
        return "pagos/cancelar";
    }

    @PostMapping("/cancelar")
    public String cancelar(@RequestParam Integer idReservacion,
                           @RequestParam String motivo,
                           @RequestParam(defaultValue = "false") boolean reembolso,
                           RedirectAttributes ra) {
        String resultado = pagoService.cancelarReservacionSP(idReservacion, motivo, reembolso);
        if (resultado.startsWith("OK")) {
            ra.addFlashAttribute("exito", resultado.replace("OK: ", ""));
        } else {
            ra.addFlashAttribute("error", resultado.replace("ERROR: ", ""));
        }
        return "redirect:/reservaciones";
    }
}
