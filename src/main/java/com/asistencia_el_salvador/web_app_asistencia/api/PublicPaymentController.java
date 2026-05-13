package com.asistencia_el_salvador.web_app_asistencia.api;

import com.asistencia_el_salvador.web_app_asistencia.dto.ApiResponse;
import com.asistencia_el_salvador.web_app_asistencia.dto.PagoRequest;
import com.asistencia_el_salvador.web_app_asistencia.dto.TransactionResult;
import com.asistencia_el_salvador.web_app_asistencia.model.AfiliadoCreadoResumen;
import com.asistencia_el_salvador.web_app_asistencia.model.AfiliadoPago;
import com.asistencia_el_salvador.web_app_asistencia.model.PagoAfiliadoApiRequest;
import com.asistencia_el_salvador.web_app_asistencia.model.Usuario;
import com.asistencia_el_salvador.web_app_asistencia.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/public_api")
public class PublicPaymentController {

        private final WompiCardService wompiCardService;

        @Autowired
        private AfiliadoService afiliadoService;
        @Autowired
        private AfiliadoPagoService afiliadoPagoService;
        @Autowired
        private UsuarioService usuarioService;
        @Autowired
        private EmailService emailService;

        private static final java.util.logging.Logger log =
                java.util.logging.Logger.getLogger(PublicPaymentController.class.getName());

        public PublicPaymentController(WompiCardService wompiCardService) {
            this.wompiCardService = wompiCardService;
        }

        @PostMapping("/procesar")
        public ResponseEntity<ApiResponse<TransactionResult>> procesarPago(
                @RequestBody PagoRequest req) {
            try {

                if (req.getIdPais()   == null) req.setIdPais("SV");
                if (req.getIdRegion() == null) req.setIdRegion("SV-SS");
                if (req.getMoneda()   == null) req.setMoneda("USD");

                if (req.getIdExterno() == null) {
                    req.setIdExterno("TXN-" + System.currentTimeMillis());
                }

                TransactionResult result = wompiCardService.procesarPago3DS(req);

                if (result == null) {
                    return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                            .body(ApiResponse.error("No se obtuvo respuesta de Wompi"));
                }

                return ResponseEntity.ok(ApiResponse.ok("Pago procesado", result));

            } catch (HttpClientErrorException e) {
                log.warning("Error Wompi (cliente): " + e.getResponseBodyAsString());
                return ResponseEntity.status(e.getStatusCode())
                        .body(ApiResponse.error("Error de Wompi: " + e.getResponseBodyAsString()));

            } catch (HttpServerErrorException e) {
                log.severe("Error Wompi (servidor): " + e.getResponseBodyAsString());
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body(ApiResponse.error("Error en servidor de Wompi"));

            } catch (Exception e) {
                log.severe("Error al procesar pago público: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.error("Error interno: " + e.getMessage()));
            }
        }


    @PostMapping("/pagos/registrar/{dui}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> registrarPagoAfiliado(
            @RequestBody PagoAfiliadoApiRequest req,
            @PathVariable String dui,
            HttpServletRequest session) {
        try {
            Integer rol = 3;


            // Solo el propio afiliado (rol 3) puede registrar su pago
            // Admins (rol 1 y 2) también pueden
            // Si quisieras restringir: if (rol == 3 && !dui.equals(req.getDui())) return sinPermiso();

            // 1. Verificar si ya existe el pago
            if (afiliadoPagoService.existePago(dui, req.getMes(), req.getAnio())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(ApiResponse.error("Ya existe un pago registrado para " + req.getMes() + "/" + req.getAnio()));
            }

            // 2. Obtener información del afiliado
            AfiliadoCreadoResumen afiliado = afiliadoService.getAfiliadoCreadoById(dui)
                    .orElse(null);
            if (afiliado == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("No se encontró el afiliado"));
            }

            // 3. Construir el pago — el voucherURL es la referencia de Wompi
            String voucherRef = req.getIdTransaccion() != null
                    ? "WOMPI-" + req.getIdTransaccion()
                    : req.getIdExterno();

            AfiliadoPago pago = new AfiliadoPago();
            pago.setDuiAfiliado(dui);
            pago.setMes(req.getMes());
            pago.setAnio(req.getAnio());
            pago.setCantidadPagada(req.getCantidadPagada());
            pago.setFormaPago(req.getFormaPago());
            pago.setPagadoPor(dui);
            pago.setCobradoPor(dui);        // pago propio desde app
            pago.setVoucherURL(voucherRef); // referencia Wompi como evidencia

            // 4. Guardar según vigencia del plan
            String vigencia = afiliado.getVigencia();

            if ("1".equals(vigencia)) {
                // Pago mensual
                afiliadoPagoService.guardarPago(pago);

            } else if ("12".equals(vigencia)) {
                // Pago anual — divide en 12 meses
                BigDecimal montoPorMes = req.getCantidadPagada()
                        .divide(new BigDecimal("12"), 2, RoundingMode.HALF_UP);

                int mesInicial = req.getMes();
                int anioActual = Integer.parseInt(req.getAnio());

                for (int i = 0; i < 12; i++) {
                    int mesActual  = mesInicial + i;
                    int anioReg    = anioActual;
                    while (mesActual > 12) { mesActual -= 12; anioReg++; }

                    if (afiliadoPagoService.existePago(dui, mesActual, String.valueOf(anioReg))) continue;

                    AfiliadoPago pagoMes = new AfiliadoPago();
                    pagoMes.setDuiAfiliado(dui);
                    pagoMes.setMes(mesActual);
                    pagoMes.setAnio(String.valueOf(anioReg));
                    pagoMes.setCantidadPagada(montoPorMes);
                    pagoMes.setFormaPago(req.getFormaPago());
                    pagoMes.setPagadoPor(dui);
                    pagoMes.setCobradoPor(dui);
                    pagoMes.setVoucherURL(i == 0 ? voucherRef : null);

                    afiliadoPagoService.guardarPago(pagoMes);
                }
            }

            // 5. Activar usuario si es su primer pago
            Usuario u = usuarioService.getUsuarioById(dui).orElse(null);
            if (u != null && !u.getActivo()) {
                u.setActivo(true);
                usuarioService.modificarDatos(dui, u);

                String emailAfiliado = afiliadoService.getAfiliadoById(dui)
                        .map(a -> a.getEmail()).orElse(null);

                if (emailAfiliado != null) {
                    emailService.enviarEmailHtml(emailAfiliado,
                            "Tus credenciales de acceso",
                            "Tu usuario es tu DUI: " + dui +
                                    " y tu contraseña es: " + emailAfiliado.split("@")[0]);

                    emailService.enviarEmailHtml(emailAfiliado,
                            "Firma tu contrato",
                            "Entra aquí para firmar tu contrato: " +
                                    "<a href='http://webappasistencia.fly.dev/firmar/nuevo/" + dui + "'>Firma aquí</a>");
                }
            }

            // 6. Respuesta
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("dui",          dui);
            body.put("mes",          req.getMes());
            body.put("anio",         req.getAnio());
            body.put("idTransaccion", req.getIdTransaccion());
            body.put("vigencia",     vigencia);

            return ResponseEntity.ok(ApiResponse.ok("Pago registrado exitosamente", body));

        } catch (Exception e) {
            return serverError(e);
        }
    }

    private <T> ResponseEntity<ApiResponse<T>> serverError(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error interno del servidor: " + e.getMessage()));
    }

}

