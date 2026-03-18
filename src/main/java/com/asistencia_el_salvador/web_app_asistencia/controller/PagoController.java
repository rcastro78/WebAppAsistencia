package com.asistencia_el_salvador.web_app_asistencia.controller;

import com.asistencia_el_salvador.web_app_asistencia.service.WompiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/pago")
public class PagoController {

    @Autowired
    private WompiService wompiService;

    @GetMapping("/confirmacion")
    public String confirmacionPago(
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String status,
            Model model) {

        if (id != null) {
            // Consultar estado de la transacción en Wompi
            Map<String, Object> transaccion = wompiService.consultarTransaccion(id);
            model.addAttribute("transaccion", transaccion);
        }

        model.addAttribute("status", status);
        return "pago-confirmacion"; // vista Thymeleaf
    }
}
