package com.asistencia_el_salvador.web_app_asistencia.controller;

import com.asistencia_el_salvador.web_app_asistencia.model.Afiliado;
import com.asistencia_el_salvador.web_app_asistencia.model.Usuario;
import com.asistencia_el_salvador.web_app_asistencia.service.AfiliadoService;
import com.asistencia_el_salvador.web_app_asistencia.service.PdfGeneratorService;
import com.asistencia_el_salvador.web_app_asistencia.service.PdfService;
import com.asistencia_el_salvador.web_app_asistencia.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/pdf")
public class PdfController {

    private final PdfService pdfService;
    private final PdfGeneratorService pdfGeneratorService;
    //Datos del afiliado
    @Autowired
    private AfiliadoService afiliadoService;
    @Autowired
    private UsuarioService usuarioService;


    public PdfController(PdfService pdfService, PdfGeneratorService pdfGeneratorService) {
        this.pdfService = pdfService;
        this.pdfGeneratorService = pdfGeneratorService;
    }

    @PostMapping("/generate")
    public ResponseEntity<byte[]> generatePdf(@RequestBody PdfRequest request,
                                              HttpSession session) {
        try {
            byte[] pdfBytes = pdfGeneratorService.generatePdfFromImageUrls(request.getImageUrls());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "duis.pdf");
            headers.setContentLength(pdfBytes.length);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/contrato/{dui}")
    public ResponseEntity<?> generarReporte(@PathVariable String dui) throws Exception {
        Afiliado afiliado = afiliadoService.getAfiliadoById(dui).get();
        int estadoContrato = afiliado.getEstadoContrato();

        if (estadoContrato == 0) {
            return ResponseEntity.badRequest()
                    .body("No se puede generar el contrato. El estado del contrato es inactivo.");
        }

        byte[] pdfBytes = pdfService.generatePdf(afiliado);

        return ResponseEntity.ok()
                .header("Content-Disposition", "inline; filename=contrato.pdf")
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }



    /**
     * Clase interna para recibir la petición
     */
    public static class PdfRequest {
        private List<String> imageUrls;
        public List<String> getImageUrls() {
            return imageUrls;
        }
        public void setImageUrls(List<String> imageUrls) {
            this.imageUrls = imageUrls;
        }
    }

}