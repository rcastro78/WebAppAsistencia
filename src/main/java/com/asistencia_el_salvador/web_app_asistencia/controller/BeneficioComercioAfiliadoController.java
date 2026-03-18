package com.asistencia_el_salvador.web_app_asistencia.controller;

import com.asistencia_el_salvador.web_app_asistencia.model.BeneficioComercioAfiliado;
import com.asistencia_el_salvador.web_app_asistencia.model.EmpresaAfiliada;
import com.asistencia_el_salvador.web_app_asistencia.repository.EmpresaAfiliadaRepository;
import com.asistencia_el_salvador.web_app_asistencia.service.BeneficioComercioAfiliadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/beneficiosComercioAfiliado")
public class BeneficioComercioAfiliadoController {

    @Autowired
    private BeneficioComercioAfiliadoService beneficioService;

    @Autowired
    private EmpresaAfiliadaRepository empresaRepository;

    // Listado de beneficios por empresa
    @GetMapping("/comercio/{nit}")
    public String listarPorEmpresa(@PathVariable String nit, Model model) {
        EmpresaAfiliada empresa = empresaRepository.findById(nit)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));

        List<BeneficioComercioAfiliado> beneficios = beneficioService.obtenerPorNitComercio(nit);
        long activos   = beneficios.stream().filter(b -> b.getEstado() == 1).count();
        long inactivos = beneficios.stream().filter(b -> b.getEstado() != 1).count();
        model.addAttribute("empresa", empresa);
        model.addAttribute("beneficios", beneficios);
        model.addAttribute("activos", activos);
        model.addAttribute("inactivos", inactivos);
        return "beneficios_comercio";
    }

    // Formulario nuevo beneficio
    @GetMapping("/nuevo/{nit}")
    public String formularioNuevo(@PathVariable String nit, Model model) {
        EmpresaAfiliada empresa = empresaRepository.findById(nit)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));

        BeneficioComercioAfiliado beneficio = new BeneficioComercioAfiliado();
        beneficio.setNitComercio(nit);

        model.addAttribute("empresa", empresa);
        model.addAttribute("beneficio", beneficio);
        model.addAttribute("esNuevo", true);
        return "beneficio_comercio_form";
    }

    // Formulario editar beneficio
    @GetMapping("/editar/{id}")
    public String formularioEditar(@PathVariable Long id, Model model) {
        BeneficioComercioAfiliado beneficio = beneficioService.obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Beneficio no encontrado"));

        EmpresaAfiliada empresa = empresaRepository.findById(beneficio.getNitComercio())
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));

        model.addAttribute("empresa", empresa);
        model.addAttribute("beneficio", beneficio);
        model.addAttribute("esNuevo", false);
        return "beneficio_comercio_form";
    }

    // Guardar (nuevo o editar)
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute BeneficioComercioAfiliado beneficio,
                          RedirectAttributes redirectAttributes) {
        try {
            beneficioService.guardar(beneficio);
            redirectAttributes.addFlashAttribute("mensaje", "Beneficio guardado exitosamente.");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al guardar el beneficio.");
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/beneficiosComercioAfiliado/comercio/" + beneficio.getNitComercio();
    }

    // Eliminar beneficio
    @GetMapping("/eliminar/{id}/{nit}")
    public String eliminar(@PathVariable Long id, @PathVariable String nit,
                           RedirectAttributes redirectAttributes) {
        try {
            beneficioService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensaje", "Beneficio eliminado correctamente.");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al eliminar el beneficio.");
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/beneficiosComercioAfiliado/comercio/" + nit;
    }

    // Cambiar estado
    @GetMapping("/estado/{id}/{nit}")
    public String cambiarEstado(@PathVariable Long id, @PathVariable String nit,
                                RedirectAttributes redirectAttributes) {
        beneficioService.obtenerPorId(id).ifPresent(b -> {
            b.setEstado(b.getEstado() == 1 ? 0 : 1);
            beneficioService.guardar(b);
        });
        redirectAttributes.addFlashAttribute("mensaje", "Estado actualizado.");
        redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        return "redirect:/beneficiosComercioAfiliado/comercio/" + nit;
    }
}
