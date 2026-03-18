package com.asistencia_el_salvador.web_app_asistencia.controller;

import com.asistencia_el_salvador.web_app_asistencia.model.BeneficioPlanDTO;
import com.asistencia_el_salvador.web_app_asistencia.model.Plan;
import com.asistencia_el_salvador.web_app_asistencia.model.ServicioEmpresaAfiliada;
import com.asistencia_el_salvador.web_app_asistencia.model.ServicioPlanEmpresa;
import com.asistencia_el_salvador.web_app_asistencia.service.PlanService;
import com.asistencia_el_salvador.web_app_asistencia.service.ServicioPlanEmpresaAfiliadaService;
import com.asistencia_el_salvador.web_app_asistencia.service.ServicioPlanEmpresaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/empresa/beneficios")
public class ServicioPlanEmpresaController {
    @Autowired
    private ServicioPlanEmpresaService servicioPlanEmpresaService;
    @Autowired
    private ServicioPlanEmpresaAfiliadaService servicioPlanEmpresaAfiliadaService;
    @Autowired
    private PlanService planService;
    @GetMapping({"/lista","/lista/"})
    public String listarBeneficiosPlanEmpresa(
                                               @RequestParam("idPlan") int idPlan,
                                               HttpSession session,
                                               Model model){
        List<BeneficioPlanDTO> beneficios =
                servicioPlanEmpresaService.findBeneficiosPlanProveedor(idPlan);
        model.addAttribute("beneficios", beneficios);
        model.addAttribute("idPlan", idPlan);
        return "beneficios_plan";
    }

    @GetMapping({"/listaAdmin","/listaAdmin/"})
    public String listarBeneficiosPlanEmpresa(@RequestParam("nit") String nit,
                                              HttpSession session,
                                              Model model){
        List<BeneficioPlanDTO> beneficios =
                servicioPlanEmpresaService.findBeneficiosPlanProveedor(nit);
        model.addAttribute("beneficios", beneficios);
        model.addAttribute("nit",nit);
        return "beneficios_empresa";
    }

    @GetMapping({"/nuevo","/nuevo/"})
    public String nuevoBeneficio(@RequestParam("nit") String nit,
                                 HttpSession session,
                                 Model model){
        List<Plan> planes = planService.listarActivos();
        model.addAttribute("servicioEmpresaAfiliada",new ServicioEmpresaAfiliada());
        model.addAttribute("planes",planes);
        model.addAttribute("esEdicion",false);
        model.addAttribute("nit",nit);
        return "beneficio_form";
    }

    @PostMapping("/guardar")
    public String guardarBeneficio(@ModelAttribute ServicioEmpresaAfiliada s,
                                   RedirectAttributes redirectAttributes){
        servicioPlanEmpresaAfiliadaService.guardar(s);
        redirectAttributes.addFlashAttribute("mensaje", "Servicio guardado exitosamente");
        return "redirect:/empresa/beneficios/listaAdmin/?nit="+s.getIdEmpresa();

    }

    // Método para MOSTRAR formulario de EDICIÓN
    @GetMapping({"/editar","/editar/"})
    public String mostrarFormularioEdicion(@RequestParam String nit,
                                           @RequestParam String id,
                                           Model model) {
        ServicioEmpresaAfiliada servicio = servicioPlanEmpresaAfiliadaService.buscarPorId(id);
        model.addAttribute("servicioEmpresaAfiliada", servicio);
        model.addAttribute("nit", nit);
        model.addAttribute("planes", planService.listarActivos());
        model.addAttribute("esEdicion", true); // BANDERA
        return "beneficio_form";
    }

    // Método para ACTUALIZAR beneficio
    @PostMapping("/editar")
    public String editarBeneficio(@RequestParam String nit,
                                  @RequestParam String id,
                                  @ModelAttribute ServicioEmpresaAfiliada s,
                                  RedirectAttributes redirectAttributes) {
        s.setIdServicio(Integer.parseInt(id));
        s.setIdEmpresa(nit);

        // Pasar tanto el ID como el objeto con los datos actualizados
        servicioPlanEmpresaAfiliadaService.editar(id, s);

        redirectAttributes.addFlashAttribute("mensaje", "Servicio actualizado exitosamente");
        return "redirect:/usuarios/comercio_dashboard";
    }


    @PostMapping("/eliminar")
    public String eliminarBeneficio(@RequestParam String nit,
                                    @RequestParam String id,
                                    RedirectAttributes redirectAttributes) {
        // Buscar el servicio existente
        ServicioEmpresaAfiliada servicio = servicioPlanEmpresaAfiliadaService.buscarPorId(id);
        servicio.setEstado(-1); // Marcar como eliminado
        servicio.setDeletedAt(LocalDateTime.now());
        // Guardar con estado actualizado
        servicioPlanEmpresaAfiliadaService.editar(id, servicio);

        redirectAttributes.addFlashAttribute("mensaje", "Servicio eliminado exitosamente");
        return "redirect:/usuarios/comercio_dashboard";
    }



}
