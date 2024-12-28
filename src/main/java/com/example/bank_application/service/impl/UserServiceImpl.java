package com.example.bank_application.service.impl;

import com.example.bank_application.config.JwtTokenProvider;
import com.example.bank_application.dto.*;
import com.example.bank_application.entity.Role;
import com.example.bank_application.entity.User;
import com.example.bank_application.repository.UserRepository;
import com.example.bank_application.utils.AccountUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
     UserRepository userRepository;

    @Autowired
     EmailService emailService;

    @Autowired
     TransactionService transactionService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Override
    public BankResponse createAccount(UserRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User newUser = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .otherName(userRequest.getOtherName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountUtils.generateAccountNumber())
                .accountBalance(BigDecimal.ZERO)
                .email(userRequest.getEmail())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .phoneNumber(userRequest.getPhoneNumber())
                .status("ACTIVE")
                .role(Role.valueOf("ROLE_ADMIN"))
                .build();

        User savedUser = userRepository.save(newUser);

        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("Account Created")
                .messageBody("Congratulations! Your account has been successfully created.\n" +
                        "Your account details are:\n" +
                        "Account Name: " + savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getOtherName() + "\n" +
                        "Account Number: " + savedUser.getAccountNumber())
                .build();
        emailService.sendEmailAlert(emailDetails);

        return BankResponse.builder()
                .responseCode(AccountUtils.SUCCESS_CODE)
                .responseMessage(AccountUtils.SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(savedUser.getAccountBalance())
                        .accountNumber(savedUser.getAccountNumber())
                        .accountName(savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getOtherName())
                        .build())
                .build();
    }


    public BankResponse login(LoginDto logindto){
        Authentication authentication=null;
        authentication=authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(logindto.getEmail(),logindto.getPassword())
        );

        EmailDetails loginAlert=EmailDetails.builder()
                .subject("you are logged in ")
                .recipient(logindto.getEmail())
                .messageBody("You are logged into your account . if not please report and change the password immediately  ")
                .build();
             emailService.sendEmailAlert(loginAlert);

     return BankResponse.builder()
             .responseCode("login Success ")
             .responseMessage(jwtTokenProvider.generateToken(authentication))
             .build();
    }


    @Override
    public BankResponse balanceEnquiry(EnquiryRequest request) {
        if (!userRepository.existsByAccountNumber(request.getAccountNumber())) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .responseMessage(AccountUtils.ACCOUNT_FOUND_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(foundUser.getAccountBalance())
                        .accountNumber(foundUser.getAccountNumber())
                        .accountName(foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName())
                        .build())
                .build();
    }

    @Override
    public BankResponse nameEnquiry(EnquiryRequest enquiryRequest) {
        if (!userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber())) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User foundUser = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .responseMessage(AccountUtils.ACCOUNT_FOUND_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName())
                        .build())
                .build();
    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest creditDebitRequest) {

        // Logic for crediting the account
        boolean isAccountExists =userRepository.existsByAccountNumber(creditDebitRequest.getAccountNumber());
       if(!isAccountExists){
           return BankResponse.builder()
                   .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                   .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                   .accountInfo(null)
                   .build();
       }

       User userToCredit =userRepository.findByAccountNumber(creditDebitRequest.getAccountNumber());
       userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(creditDebitRequest.getAmount()));
       userRepository.save(userToCredit);

       // save the transaction

        TransactionDto transactionDto=TransactionDto.builder()
                .accountNumber(userToCredit.getAccountNumber())
                .amount(creditDebitRequest.getAmount())
                .transactionType("CREDIT")
                .build();


        transactionService.saveTransaction(transactionDto);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREDITED_CODE)
                .responseMessage(AccountUtils.ACCOUNT_CREDITED_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(userToCredit.getFirstName() + " " + userToCredit.getLastName() + " " + userToCredit.getOtherName())
                        .accountBalance(creditDebitRequest.getAmount())
                        .accountNumber(creditDebitRequest.getAccountNumber())
                        .build())
                .build();
    }



    @Override
    public BankResponse debitAccount(CreditDebitRequest creditDebitRequest) {

        // Logic for crediting the account
        boolean isAccountExists =userRepository.existsByAccountNumber(creditDebitRequest.getAccountNumber());
        if(!isAccountExists){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User userToDebit = userRepository.findByAccountNumber(creditDebitRequest.getAccountNumber());
        BigDecimal availableBalance = userToDebit.getAccountBalance();
        BigDecimal debitAmount = creditDebitRequest.getAmount();



        if (availableBalance.compareTo(debitAmount) < 0) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        else {

            userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(creditDebitRequest.getAmount()));
            userRepository.save(userToDebit);

            // save the transaction

            TransactionDto transactionDto=TransactionDto.builder()
                    .accountNumber(userToDebit.getAccountNumber())
                    .amount(creditDebitRequest.getAmount())
                    .transactionType("DEBIT")
                    .build();
            transactionService.saveTransaction(transactionDto);

            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DEBITED_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_DEBITED_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountName(userToDebit.getFirstName() + " " + userToDebit.getLastName() + " " + userToDebit.getOtherName())
                            .accountBalance(creditDebitRequest.getAmount())
                            .accountNumber(creditDebitRequest.getAccountNumber())
                            .build())
                    .build();
        }


    }

    @Override
    public BankResponse transferRequest(TransferRequest transferRequest) {


        boolean isSourceAccountExists =userRepository.existsByAccountNumber(transferRequest.getSourceAccountNumber());
        if(!isSourceAccountExists){
            return BankResponse.builder()
                    .responseCode(AccountUtils.SOURCE_ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.SOURCE_ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        boolean isDestinationAccountExists =userRepository.existsByAccountNumber(transferRequest.getDestinationAccountNumber());
        if(!isDestinationAccountExists){
            return BankResponse.builder()
                    .responseCode(AccountUtils.DESTINATION_ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.DESTINATION_ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }




        User SourceAccountUser = userRepository.findByAccountNumber(transferRequest.getSourceAccountNumber());
        User DestinationAccountUser = userRepository.findByAccountNumber(transferRequest.getDestinationAccountNumber());




        BigDecimal SourceavailableBalance = SourceAccountUser.getAccountBalance();
        BigDecimal debitAmount = transferRequest.getAmount();


        if (SourceavailableBalance.compareTo(debitAmount) < 0) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        else {

            SourceAccountUser.setAccountBalance(SourceAccountUser.getAccountBalance().subtract(transferRequest.getAmount()));
            userRepository.save(SourceAccountUser);

            EmailDetails debitAlertEmail = EmailDetails.builder()
                    .recipient(SourceAccountUser.getEmail())
                    .subject("Amount Debited from ACCOUNT")
                    .messageBody("The sum of " + transferRequest.getAmount()+" has been debited from your account ! with account number "+ "Account Number: " + SourceAccountUser.getAccountNumber() +".\n"
                            +"your current account "+ SourceAccountUser.getAccountBalance()
                    )
                    .build();
            emailService.sendEmailAlert(debitAlertEmail);


            // save the transaction for SourceAccountUser
            TransactionDto transactionDto_D=TransactionDto.builder()
                    .accountNumber(SourceAccountUser.getAccountNumber())
                    .amount(transferRequest.getAmount())
                    .transactionType("DEBIT")
                    .build();
            transactionService.saveTransaction(transactionDto_D);


            DestinationAccountUser.setAccountBalance(DestinationAccountUser.getAccountBalance().add(transferRequest.getAmount()));
            userRepository.save(DestinationAccountUser);


            EmailDetails creditAlertEmail = EmailDetails.builder()
                    .recipient(DestinationAccountUser.getEmail())
                    .subject("Amount credited from ACCOUNT")
                    .messageBody("The sum of " + transferRequest.getAmount()+" has been credited to your account ! for account number "+ "Account Number: " + DestinationAccountUser.getAccountNumber() +".\n"
                            +"your current account "+ DestinationAccountUser.getAccountBalance()
                    )
                    .build();
            emailService.sendEmailAlert(creditAlertEmail);


            // save the transaction for DestinationAccountUser
            TransactionDto transactionDto_C=TransactionDto.builder()
                    .accountNumber(DestinationAccountUser.getAccountNumber())
                    .amount(transferRequest.getAmount())
                    .transactionType("CREDIT")
                    .build();
            transactionService.saveTransaction(transactionDto_C);

            return BankResponse.builder()
                    .responseCode(AccountUtils.TRANSFER_SUCCESS_CODE)
                    .responseMessage(AccountUtils.TRANSFER_SUCCESS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }







    }


}
