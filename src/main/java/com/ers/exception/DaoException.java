package com.ers.exception;

public class DaoException extends RuntimeException{
    public DaoException(String message,Throwable t){
        super(message,t);
    }
    public DaoException(String message){
        super(message);
    }
}
