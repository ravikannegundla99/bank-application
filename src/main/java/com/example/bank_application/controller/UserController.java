package com.example.bank_application.controller;

import com.example.bank_application.dto.*;
import com.example.bank_application.service.impl.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Tag(name="User Account Management API's")
public class UserController {

    @Autowired
    private UserService userService;


//    New user Account
    @Operation(
            summary = "Create New user Account",
            description = "Creating a new user and Assigning an account Number"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Http Status 201 Created"
    )
    @PostMapping
    public BankResponse createAccount(@RequestBody UserRequest userRequest) {
        return userService.createAccount(userRequest);
    }


    //    get Token
    @Operation(
            summary = "Get a Token for the user",
            description = "Get a Token for the user with email and password"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 Success "
    )
    @PostMapping("/login")
    public BankResponse login(@RequestBody LoginDto loginDto) {
        return userService.login(loginDto);
    }


//    Name Enquiry
    @Operation(
            summary = "Name Enquiry",
            description = "Get the name with the account Number "
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 Success"
    )
    @PostMapping("nameEnquiry")
    public BankResponse nameEnquiry(@RequestBody EnquiryRequest request) {
        return userService.nameEnquiry(request);
    }



//    Balance Enquiry
        @Operation(
        summary = "Balance Enquiry",
        description = "Get the balance with the account Number "
        )
        @ApiResponse(
        responseCode = "200",
        description = "Http Status 200 Success"
       )
    @PostMapping("balanceEnquiry")
    public BankResponse balanceEnquiry(@RequestBody EnquiryRequest enquiryRequest) {
        return userService.balanceEnquiry(enquiryRequest);
    }


//    Credit specific amount  to Account
    @Operation(
            summary = "Credit specific amount  to Account",
            description = "Credit specific amount to Account with the account Number "
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 Success"
    )
    @PostMapping("credit")
    public BankResponse creditAccount(@RequestBody CreditDebitRequest creditDebitRequest) {
        return userService.creditAccount(creditDebitRequest);
    }



    //    Debit specific amount  to Account
    @Operation(
            summary = "Debit specific amount  to Account",
            description = "Debit specific amount to Account with the account Number "
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 Success"
    )
    @PostMapping("debit")
    public BankResponse debitAccount(@RequestBody CreditDebitRequest creditDebitRequest) {
        return userService.debitAccount(creditDebitRequest);
    }



    //    Transfer specific amount from one Account to other Account
    @Operation(
            summary = "Transfer specific amount from one Account to other Account",
            description = "Debit specific amount from one Account to other Account"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 Success"
    )
    @PostMapping("transfer")
    public BankResponse transfer(@RequestBody TransferRequest transferRequest) {
        return userService.transferRequest(transferRequest);
    }



}
