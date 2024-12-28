package com.example.bank_application.utils;

import java.time.Year;

public class AccountUtils {

    public static  final String ACCOUNT_EXISTS_CODE="001";

    public static  final String ACCOUNT_EXISTS_MESSAGE="This User already has an account";

    public static  final String SUCCESS_CODE="201";

    public static  final String SUCCESS_MESSAGE="Account created";


    public static  final String ACCOUNT_NOT_EXIST_CODE="003";

    public static  final String ACCOUNT_NOT_EXIST_MESSAGE="User not exists with the account number";


    public static  final String ACCOUNT_FOUND_CODE="004";

    public static  final String ACCOUNT_FOUND_MESSAGE="User  exists with the account number";



    public static  final String ACCOUNT_CREDITED_CODE="005";

    public static  final String ACCOUNT_CREDITED_MESSAGE="Amount was credited to the account";

    public static  final String ACCOUNT_DEBITED_CODE="006";

    public static  final String ACCOUNT_DEBITED_MESSAGE="Amount was Debited to the account";


    public static  final String INSUFFICIENT_BALANCE_CODE="007";

    public static  final String INSUFFICIENT_BALANCE_MESSAGE="Insufficient Balance in the Account";


    public static  final String SOURCE_ACCOUNT_NOT_EXIST_CODE="008";

    public static  final String SOURCE_ACCOUNT_NOT_EXIST_MESSAGE="User not exists with the account number";

    public static  final String DESTINATION_ACCOUNT_NOT_EXIST_CODE="009";

    public static  final String DESTINATION_ACCOUNT_NOT_EXIST_MESSAGE="User not exists with the account number";


    public static  final String TRANSFER_SUCCESS_CODE="010";

    public static  final String TRANSFER_SUCCESS_MESSAGE="Transfer was successful ";


    public static String generateAccountNumber(){

//     current year and any random 6 digits

        Year currentyear= Year.now();
        int min=100000;
        int max=999999;

        int randNumber = (int) Math.floor(Math.random()*(max - min+1) + min);

        String year=String.valueOf(currentyear);
        String randomNumber=String.valueOf(randNumber);

        return year + randomNumber;

    }


}
