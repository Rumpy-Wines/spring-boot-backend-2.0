package com.example.rumpy.exceptions;

public class PaymentServiceException extends Exception {
    public PaymentServiceException(String message){
        super(message);
    }

    public PaymentServiceException(){super();}
    public PaymentServiceException(Throwable cause){super(cause);}
    public PaymentServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
