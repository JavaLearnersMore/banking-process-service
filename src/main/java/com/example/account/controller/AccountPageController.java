package com.example.account.controller;

import com.example.account.dto.AccountPageRequest;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Controller
public class AccountPageController {

    private final RestTemplate restTemplate;

    public AccountPageController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/accounts")
    public String accountPage() {
        return "account-form";
    }

    @PostMapping("/accounts/create")
    public String createAccount(@ModelAttribute AccountPageRequest request) {

        String url = "http://localhost:8084/core/api/v1/accounts";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);


        HttpEntity<AccountPageRequest> entity =
                new HttpEntity<>(request);

        ResponseEntity<String> response = restTemplate.postForEntity(
                        url,
                        entity,
                        String.class
                );

        System.out.println(
                "API Response: " + response.getBody()
        );

        return "account-success";
    }
}