package com.hotel.sistema.controller;

import com.hotel.sistema.repository.RolRepository;
import com.hotel.sistema.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired private UsuarioService usuarioService;
    @Autowired private RolRepository rolRepository;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        return "usuarios/lista";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, RedirectAttributes ra) {
        usuarioService.eliminar(id);
        ra.addFlashAttribute("exito", "Usuario eliminado.");
        return "redirect:/usuarios";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Integer id, Model model) {
        usuarioService.buscarPorId(id).ifPresent(u -> model.addAttribute("usuario", u));
        model.addAttribute("roles", rolRepository.findAll());
        return "usuarios/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute com.hotel.sistema.entity.Usuario usuario,
                          @RequestParam(value = "idRol", required = false) Integer idRol,
                          RedirectAttributes ra) {
        if (idRol != null)
            rolRepository.findById(idRol).ifPresent(usuario::setRol);
        usuarioService.actualizar(usuario);
        ra.addFlashAttribute("exito", "Usuario actualizado.");
        return "redirect:/usuarios";
    }
}
