package com.hotel.sistema.controller;

import com.hotel.sistema.entity.Habitacion;
import com.hotel.sistema.service.HabitacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/habitaciones")
public class HabitacionController {

    @Autowired
    private HabitacionService habitacionService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("habitaciones", habitacionService.listarTodas());
        return "habitaciones/lista";
    }

    @GetMapping("/nueva")
    public String nuevaForm(Model model) {
        model.addAttribute("habitacion", new Habitacion());
        return "habitaciones/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Habitacion habitacion, RedirectAttributes ra) {
        habitacionService.guardar(habitacion);
        ra.addFlashAttribute("exito", "Habitación guardada correctamente.");
        return "redirect:/habitaciones";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Integer id, Model model) {
        habitacionService.buscarPorId(id).ifPresent(h -> model.addAttribute("habitacion", h));
        return "habitaciones/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, RedirectAttributes ra) {
        habitacionService.eliminar(id);
        ra.addFlashAttribute("exito", "Habitación eliminada.");
        return "redirect:/habitaciones";
    }
}
