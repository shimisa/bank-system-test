package com.example.bank_system.controller;

import com.example.bank_system.dto.TransferRequest;
import com.example.bank_system.dto.TransactionApiResponse;
import com.example.bank_system.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/transfer")
    public ResponseEntity<TransactionApiResponse> processTransfer(
            @Valid @RequestBody TransferRequest request) {
        TransactionApiResponse response = transactionService.processTransfer(request);
        return ResponseEntity.ok(response);
    }

}
