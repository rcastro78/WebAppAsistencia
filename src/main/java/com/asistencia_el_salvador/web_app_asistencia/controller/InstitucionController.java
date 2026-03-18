package com.asistencia_el_salvador.web_app_asistencia.controller;

import com.asistencia_el_salvador.web_app_asistencia.model.EmpresaAfiliada;
import com.asistencia_el_salvador.web_app_asistencia.model.Institucion;
import com.asistencia_el_salvador.web_app_asistencia.service.InstitucionService;
import com.asistencia_el_salvador.web_app_asistencia.service.PaisService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/instituciones")
public class InstitucionController {
    private final InstitucionService institucionService;
    private final PaisService paisService;
    public InstitucionController(InstitucionService institucionService,
                                 PaisService paisService) {
        this.institucionService = institucionService;
        this.paisService = paisService;
    }

    @GetMapping("/nuevo")
    public String showCreateInstitucionForm(Model model) {
        model.addAttribute("institucion", new Institucion());
        model.addAttribute("paises", paisService.listarTodos());
        return "institucion";
    }
    //Metodo
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Institucion institucion) {
        institucionService.saveInstitucion(institucion);
        return "redirect:/instituciones/"; // redirige a la lista de instituciones
    }

    //Metodo
    @PostMapping("/editar")
    public String actualizar(@ModelAttribute Institucion institucion) {
        // Llama al servicio para actualizar la institución
        institucionService.updateInstitucion(institucion.getId(), institucion);
        // Redirige al listado de instituciones después de actualizar
        return "redirect:/instituciones/";
    }
    //Pagina
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable String id, Model model) {
        Institucion institucion = institucionService.getInstitucion(id)
                .orElseThrow(() -> new RuntimeException("Institucion no encontrada con ID: " + id));
        model.addAttribute("paises", paisService.listarTodos());
        model.addAttribute("institucion", institucion);
        return "editarInstitucion"; // vista que muestra el formulario de edición
    }

    @GetMapping("/")
    public String listarInstituciones(@RequestParam(defaultValue = "0") int page, Model model){
        Page<Institucion> instituciones = institucionService.listarPaginados(PageRequest.of(page, 10));
        model.addAttribute("instituciones", instituciones.getContent());
        model.addAttribute("paginaActual", page);
        model.addAttribute("totalPaginas", instituciones.getTotalPages());

        return "instituciones";
    }
}
