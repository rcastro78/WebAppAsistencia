package com.asistencia_el_salvador.web_app_asistencia.controller;

import com.asistencia_el_salvador.web_app_asistencia.service.QRCodeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class QrController {

    private final QRCodeService qrCodeService;

    public QrController(QRCodeService qrCodeService) {
        this.qrCodeService = qrCodeService;
    }

    @GetMapping("/carnet")
    public String showQr(Model model, String text) throws Exception {
        String qrBase64 = qrCodeService.generateQrOnImage(text, "/static/images/manos_azul.png");
        model.addAttribute("qrImage", qrBase64);
        return "qr-view";
    }
}
