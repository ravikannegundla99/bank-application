package com.example.bank_application.service.impl;

import com.example.bank_application.dto.EmailDetails;

public interface EmailService {


void sendEmailAlert(EmailDetails emailDetails);


void sendEmailwithAttachment(EmailDetails emailDetails);


}
