package com.example.bank_application.service.impl;

import com.example.bank_application.dto.*;


public interface UserService {

    BankResponse createAccount(UserRequest userRequest);

    BankResponse balanceEnquiry(EnquiryRequest enquiryRequest);

    BankResponse nameEnquiry(EnquiryRequest enquiryRequest);

    BankResponse creditAccount(CreditDebitRequest creditDebitRequest);

    BankResponse debitAccount(CreditDebitRequest creditDebitRequest);


    BankResponse transferRequest(TransferRequest transferRequest);

    BankResponse login(LoginDto loginDto);
}
