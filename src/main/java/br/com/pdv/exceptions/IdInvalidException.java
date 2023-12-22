package br.com.pdv.exceptions;

public class IdInvalidException extends RuntimeException {
    public IdInvalidException(String message){
        super(message);
    }
}
