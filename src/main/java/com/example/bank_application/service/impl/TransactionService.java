package com.example.bank_application.service.impl;

import com.example.bank_application.dto.TransactionDto;
import com.example.bank_application.entity.Transaction;

public interface TransactionService {


    void saveTransaction(TransactionDto transactionDto);

}
