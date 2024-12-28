package com.example.bank_application.service.impl;


import com.example.bank_application.dto.TransactionDto;
import com.example.bank_application.entity.Transaction;
import com.example.bank_application.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class TransactionServiceImpl  implements  TransactionService{

    @Autowired
    TransactionRepository transactionRepository;

    @Override
    public void saveTransaction(TransactionDto transactionDto) {

        Transaction transaction = Transaction.builder()
                .transactionType(transactionDto.getTransactionType())
                .accountNumber(transactionDto.getAccountNumber())
                .status("SUCCESS")
                .amount(transactionDto.getAmount())
                .build();

transactionRepository.save(transaction);
System.out.println("Transaction saved successfully ");

    }






}
