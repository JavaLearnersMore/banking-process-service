package com.example.account.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.account.dto.ApiResponse;
import com.example.account.dto.TransactionRequest;
import com.example.account.dto.TransactionResponse;
import com.example.account.service.TransactionService;

@RestController
@RequestMapping("/core/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/post")
    public ResponseEntity<TransactionResponse> postTransaction( @RequestBody TransactionRequest request) {

        TransactionResponse response = transactionService.postTransaction(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/{externalRef}/reverse")
    public ResponseEntity<ApiResponse> reverseTransaction(@PathVariable String externalRef, Authentication authentication) {

        String initiatedBy = authentication.getName();

        transactionService.reverseTransaction( externalRef, initiatedBy);
        
        ApiResponse response = new ApiResponse(true,HttpStatus.OK.value(),
                "Transaction reversed successfully",externalRef
        );

        return ResponseEntity.ok(response);
    }
}