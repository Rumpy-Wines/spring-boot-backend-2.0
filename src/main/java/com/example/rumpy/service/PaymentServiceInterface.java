package com.example.rumpy.service;

import com.example.rumpy.exceptions.PaymentServiceException;
import com.example.rumpy.model.TransactionPaymentInterface;
import com.example.rumpy.model.TransactionStatus;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public interface PaymentServiceInterface {
    <T extends TransactionPaymentInterface> String initializeTransaction(T transaction, String callbackUrl) throws PaymentServiceException;
    TransactionStatus getTransactionStatus(TransactionPaymentInterface transaction) throws PaymentServiceException;
    void webhook(JSONObject requestJSON, HttpServletRequest request);
}//end interface PaymentServiceInterface
