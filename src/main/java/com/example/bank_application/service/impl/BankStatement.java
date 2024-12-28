package com.example.bank_application.service.impl;


import com.example.bank_application.dto.EmailDetails;
import com.example.bank_application.entity.Transaction;
import com.example.bank_application.entity.User;
import com.example.bank_application.repository.TransactionRepository;
import com.example.bank_application.repository.UserRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Phaser;

@Component
@AllArgsConstructor
@Slf4j
public class BankStatement {


    private TransactionRepository transactionRepository;
    private UserRepository userRepository;

    private EmailService emailService;

    private static final String FILE="D:\\some files\\extrafiles\\myRecentStatement.pdf";

    /*
    list all transactions
    generated the PDF file
    Send the file in email service
    */

     public List<Transaction> generateStatement(String accountNumber,String startDate , String endDate) throws FileNotFoundException, DocumentException {

         LocalDate start=LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
         LocalDate end=LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);

         List<Transaction> transactionList = transactionRepository.findAll().stream().filter(transaction ->transaction.getAccountNumber().equals(accountNumber) )
                 .filter(transaction -> transaction.getCreatedAt().isAfter(start))
                 .filter(transaction -> transaction.getCreatedAt().isBefore(end)).toList();

         User user =userRepository.findByAccountNumber(accountNumber);
         String customerName=user.getFirstName()+ user.getLastName()+user.getOtherName();

         Rectangle statementSize=new Rectangle(PageSize.A4);
         Document document=new Document(statementSize);
         log.info("Setting the size of the document");
         OutputStream outputStream =new FileOutputStream(FILE);
         PdfWriter.getInstance(document,outputStream);
         document.open();


         PdfPTable bankInfoTable =new PdfPTable(1);
         PdfPCell bankName =new PdfPCell(new Phrase("ABC_BANK"));
         bankName.setBorder(0);
//         bankName.setBackgroundColor(20f);
         bankName.setBackgroundColor(new BaseColor(200, 200, 255));

         PdfPCell bankAdrress= new PdfPCell(new Phrase("712 S ELM ST, Denton,TX"));

         bankAdrress.setBorder(0);


         bankInfoTable.addCell(bankName);
         bankInfoTable.addCell(bankAdrress);

         PdfPTable statementInfo =new PdfPTable(2);
         PdfPCell customerInfo =new PdfPCell( new Phrase("Start Date : "+ startDate));
         customerInfo.setBorder(0);
         PdfPCell statement =new PdfPCell(new Phrase("STATEMENT OF ACCOUNT" ));
         statement.setBorder(0);
         PdfPCell EndDate=new PdfPCell(new Phrase("END Date"+ endDate));
         EndDate.setBorder(0);

         PdfPCell name=new PdfPCell(new Phrase("Customer Name"+ customerName));
         name.setBorder(0);

         PdfPCell space=new PdfPCell();

         PdfPCell address=new PdfPCell(new Phrase("Customer Address "+ user.getAddress()));
         name.setBorder(0);



         PdfPTable transactionsTable =new PdfPTable(4);
         PdfPCell date=new PdfPCell(new Phrase("DATE"));
         date.setBorder(0);
         date.setBackgroundColor(new BaseColor(0, 0, 255));

         PdfPCell transactionType=new PdfPCell(new Phrase("TRANSACTION TYPE"));
         transactionType.setBorder(0);
         transactionType.setBackgroundColor(new BaseColor(0, 0, 255));

         PdfPCell transactionAmount=new PdfPCell(new Phrase("TRANSACTION AMOUNT"));
         transactionAmount.setBorder(0);
         transactionAmount.setBackgroundColor(new BaseColor(0, 0, 255));


         PdfPCell status=new PdfPCell(new Phrase("STATUS"));
         status.setBorder(0);
         status.setBackgroundColor(new BaseColor(0, 0, 255));


         transactionsTable.addCell(date);
         transactionsTable.addCell(transactionType);
         transactionsTable.addCell(transactionAmount);
         transactionsTable.addCell(status);


         transactionList.forEach(transaction -> {
             transactionsTable.addCell(new Phrase(transaction.getCreatedAt().toString()));
             transactionsTable.addCell(new Phrase(transaction.getTransactionType()));
             transactionsTable.addCell(new Phrase(transaction.getAmount().toString()));
             transactionsTable.addCell(new Phrase(transaction.getStatus()));
         });



        statementInfo.addCell(customerInfo);
        statementInfo.addCell(statement);
        statementInfo.addCell(endDate);
        statementInfo.addCell(name);
        statementInfo.addCell(space);
        statementInfo.addCell(address);
        statementInfo.addCell(space);



        document.add(bankInfoTable);
        document.add(statementInfo);
        document.add(transactionsTable);
        document.close();


         EmailDetails emailDetails=EmailDetails.builder()
                 .recipient(user.getEmail())
                 .subject("Statement of the Account")
                 .messageBody(" please kindly find the attachment for your account for the requested dates ")
                 .attachment(FILE)
                 .build();

         emailService.sendEmailwithAttachment(emailDetails);




         return  transactionList;
     }





}
