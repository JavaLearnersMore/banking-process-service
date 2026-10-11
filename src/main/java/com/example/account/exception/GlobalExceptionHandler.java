package com.example.account.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.account.dto.ApiResponse;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleAccountAlreadyExists(AccountAlreadyExistsException ex) {

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                ex.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                message
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }
    
    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<Map<String, Object>>
    handleInsufficientFunds(
            InsufficientFundsException ex) {

        return buildResponse(
                HttpStatus.UNPROCESSABLE_ENTITY,
                ex.getMessage()
        );
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<Map<String, Object>>
    handleAccountNotFound(
            AccountNotFoundException ex) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>>
    handleBadRequest(
            IllegalArgumentException ex) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>>
    handleIllegalState(
            IllegalStateException ex) {

        return buildResponse(
                HttpStatus.UNPROCESSABLE_ENTITY,
                ex.getMessage()
        );
    }

    private ResponseEntity<Map<String, Object>>
    buildResponse(
            HttpStatus status,
            String message) {

        Map<String, Object> response = new LinkedHashMap<>();

        response.put(
                "timestamp",
                LocalDateTime.now());

        response.put(
                "status",
                status.value());

        response.put(
                "error",
                status.getReasonPhrase());

        response.put(
                "message",
                message);

        response.put("path","/core/api/v1/transactions/post");

        return ResponseEntity
                .status(status)
                .body(response);
    }
    
    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<ApiResponse> handleTransactionNotFound(
            TransactionNotFoundException ex) {

        ApiResponse response = new ApiResponse(
                false,
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                null
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }


    @ExceptionHandler(InvalidTransactionStatusException.class)
    public ResponseEntity<ApiResponse> handleInvalidTransactionStatus(
            InvalidTransactionStatusException ex) {

        ApiResponse response = new ApiResponse(
                false,
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                null
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }


    @ExceptionHandler(TransactionAlreadyReversedException.class)
    public ResponseEntity<ApiResponse> handleAlreadyReversed(
            TransactionAlreadyReversedException ex) {

        ApiResponse response = new ApiResponse(
                false,
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                null
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGenericException(
            Exception ex) {

        ApiResponse response = new ApiResponse(
                false,
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal server error",
                null
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

}
