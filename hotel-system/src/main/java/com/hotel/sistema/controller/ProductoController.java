package com.hotel.sistema.controller;

import com.hotel.sistema.entity.Producto;
import com.hotel.sistema.repository.UsuarioRepository;
import com.hotel.sistema.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    @Autowired private ProductoService  productoService;
    @Autowired private UsuarioRepository usuarioRepository;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("productos", productoService.listarTodos());
        return "productos/lista";
    }

    @GetMapping("/nuevo")
    public String nuevoForm(Model model) {
        model.addAttribute("producto", new Producto());
        return "productos/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Producto producto, RedirectAttributes ra) {
        productoService.guardar(producto);
        ra.addFlashAttribute("exito", "Producto guardado correctamente.");
        return "redirect:/productos";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Integer id, Model model) {
        productoService.buscarPorId(id).ifPresent(p -> model.addAttribute("producto", p));
        return "productos/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, RedirectAttributes ra) {
        productoService.eliminar(id);
        ra.addFlashAttribute("exito", "Producto eliminado.");
        return "redirect:/productos";
    }

    @GetMapping("/vender/{id}")
    public String venderForm(@PathVariable Integer id, Model model) {
        productoService.buscarPorId(id).ifPresent(p -> model.addAttribute("producto", p));
        // Cargar lista de usuarios para seleccionar a quién vender
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "productos/vender";
    }

    @PostMapping("/vender")
    public String vender(@RequestParam Integer idProducto,
                         @RequestParam Integer idUsuario,
                         @RequestParam Integer cantidad,
                         RedirectAttributes ra) {
        String resultado = productoService.registrarVentaSP(idUsuario, idProducto, cantidad);
        if (resultado.startsWith("OK")) {
            ra.addFlashAttribute("exito", resultado.replace("OK: ", ""));
        } else {
            ra.addFlashAttribute("error", resultado.replace("ERROR: ", ""));
        }
        return "redirect:/productos";
    }
}
