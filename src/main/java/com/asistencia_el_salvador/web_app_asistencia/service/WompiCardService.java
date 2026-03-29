package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.dto.PagoRequest;
import com.asistencia_el_salvador.web_app_asistencia.dto.TransactionResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WompiCardService {

    private final WompiAuthService wompiAuthService;
    private final RestTemplate restTemplate;

    private static final String URL_3DS = "https://api.wompi.sv/TransaccionCompra/3DS";

    public WompiCardService(WompiAuthService wompiAuthService, RestTemplate restTemplate) {
        this.wompiAuthService = wompiAuthService;
        this.restTemplate = restTemplate;
    }

    public TransactionResult procesarPago3DS(PagoRequest req){
        String accessToken = wompiAuthService.getToken().getAccess_token();
        String numeroLimpio = req.getTarjetaCreditoDebido().getNumeroTarjeta().replace(" ", "");
        req.getTarjetaCreditoDebido().setNumeroTarjeta(numeroLimpio);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        try {
            ObjectMapper mapper = new ObjectMapper();
            System.out.println("JSON enviado a Wompi: " + mapper.writeValueAsString(req));
            System.out.println("Token: " + accessToken);
        }catch (JsonProcessingException e){

        }

        HttpEntity<PagoRequest> request = new HttpEntity<>(req, headers);

        ResponseEntity<TransactionResult> response = restTemplate.postForEntity(
                URL_3DS,
                request,
                TransactionResult.class
        );

        return response.getBody();
    }
}
