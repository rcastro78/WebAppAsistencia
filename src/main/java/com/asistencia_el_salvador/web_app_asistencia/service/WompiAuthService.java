package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.config.WompiConfig;
import com.asistencia_el_salvador.web_app_asistencia.dto.WompiTokenResult;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


@Service
public class WompiAuthService {

    private final WompiConfig wompiConfig;
    private final RestTemplate restTemplate;

    private static final String TOKEN_URL = "https://id.wompi.sv/connect/token";

    public WompiAuthService(WompiConfig wompiConfig, RestTemplate restTemplate) {
        this.wompiConfig = wompiConfig;
        this.restTemplate = restTemplate;
    }

    public WompiTokenResult getToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        System.out.println("client_id: " + wompiConfig.getPublicKey());
        System.out.println("client_secret: " + wompiConfig.getSecretKey());

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", wompiConfig.getPublicKey());
        body.add("client_secret", wompiConfig.getSecretKey());
        body.add("audience", "wompi_api");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<WompiTokenResult> response = restTemplate.postForEntity(
                TOKEN_URL,
                request,
                WompiTokenResult.class
        );

        return response.getBody();
    }
}
