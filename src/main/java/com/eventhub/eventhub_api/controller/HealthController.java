package com.eventhub.eventhub_api.controller;

import com.eventhub.eventhub_api.dto.HealthResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HealthController {
    @Value("${app.version}")
    private String version;

    @Value("${app.name}")
    private String appName;

    @GetMapping("/health")
    public ResponseEntity<HealthResponse> healthCheck() {
        HealthResponse response = new HealthResponse();

        response.setStatus("UP");
        response.setVersion(version);
        response.setAppName(appName);
        return ResponseEntity.ok(response);
    }
}
