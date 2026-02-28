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

    @GetMapping("/health")
    public ResponseEntity<HealthResponse> healthCheck() {
        HealthResponse response = new HealthResponse();

        response.setStatus("UP");
        response.setVersion("1.0.0");
        response.setAppName("EventHub API");
        return ResponseEntity.ok(response);
    }
}
