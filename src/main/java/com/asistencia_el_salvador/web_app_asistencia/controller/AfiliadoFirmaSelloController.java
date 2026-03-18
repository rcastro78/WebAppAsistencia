package com.asistencia_el_salvador.web_app_asistencia.controller;

import com.asistencia_el_salvador.web_app_asistencia.model.Afiliado;
import com.asistencia_el_salvador.web_app_asistencia.model.AfiliadoFirmaSello;
import com.asistencia_el_salvador.web_app_asistencia.model.AfiliadoHogar;
import com.asistencia_el_salvador.web_app_asistencia.service.AfiliadoFirmaSelloService;
import com.asistencia_el_salvador.web_app_asistencia.service.AfiliadoService;
import com.asistencia_el_salvador.web_app_asistencia.service.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//Controller para solicitar firma al afiliado
@Controller
@RequestMapping("/firmar")
public class AfiliadoFirmaSelloController {
    @Autowired
    private AfiliadoFirmaSelloService afiliadoFirmaSelloService;
    @Autowired
    private AfiliadoService afiliadoService;
    @Autowired
    private EmailService emailService;
    @GetMapping("/nuevo/{dui}")
    public String firmar(@PathVariable String dui,
                         Model model,
                         HttpSession session,
                         RedirectAttributes redirectAttributes){
        AfiliadoFirmaSello afiliadoFirmaSello = new AfiliadoFirmaSello();
        afiliadoFirmaSello.setDui(dui);
        model.addAttribute("afiliadoFirmaSello", afiliadoFirmaSello);
        model.addAttribute("dui", dui);
        return "afiliado_firma";
    }

    @PostMapping("/guardar")
    public String guardarAfiliadoFirma(@ModelAttribute AfiliadoFirmaSello afiliadoFirmaSello,
                                       RedirectAttributes redirectAttributes,
                                       HttpSession session){
        afiliadoFirmaSelloService.guardar(afiliadoFirmaSello);
        //Activar el contrato
        afiliadoService.activarContrato(afiliadoFirmaSello.getDui());

        //Envir email de activacion de contrato
        Afiliado afiliado = afiliadoService.getAfiliadoById(afiliadoFirmaSello.getDui()).get();
        try {
            emailService.enviarEmailHtml(afiliado.getEmail(), "Contrato", "Te informamos que tu contrato ya se encuentra activo");
        }catch (Exception exception){}
        // Redirigir al siguiente paso
        return "redirect:/afiliado/firmado";
    }

    @PostMapping("/sellar")
    public String guardarAfiliadoSello(@ModelAttribute AfiliadoFirmaSello afiliadoFirmaSello,
                                       RedirectAttributes redirectAttributes,
                                       HttpSession session
    ){
        afiliadoFirmaSello.setSello(1);
        afiliadoFirmaSelloService.guardar(afiliadoFirmaSello);
        redirectAttributes.addFlashAttribute("mensaje",
                "Sello del afiliado colocado correctamente");
        return "redirect:/afiliado/sellado";
    }


}
