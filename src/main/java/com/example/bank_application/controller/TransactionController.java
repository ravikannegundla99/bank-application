package com.example.bank_application.controller;


import com.example.bank_application.dto.BankResponse;
import com.example.bank_application.dto.UserRequest;
import com.example.bank_application.entity.Transaction;
import com.example.bank_application.service.impl.BankStatement;
import com.example.bank_application.service.impl.TransactionService;
import com.example.bank_application.service.impl.UserService;
import com.itextpdf.text.DocumentException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/api/bankStatement")
@Tag(name="User Account Management API's")
public class TransactionController {

    @Autowired
    private BankStatement bankStatement;


    //    New user Account
    @Operation(
            summary = "Get Bank Statement ",
            description = "Get Bank Statement in the Given date Range"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 201 Success"
    )
    @GetMapping
    public List<Transaction> generateBankStatement(@RequestParam String accountNumber,
                                                   @RequestParam String startDate,
                                                   @RequestParam String endDate ) throws DocumentException, FileNotFoundException {
        return bankStatement.generateStatement(accountNumber,startDate,endDate);
    }


}
