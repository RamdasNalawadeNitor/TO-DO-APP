package com.example.demo.customException;

public class CustomException extends  RuntimeException{

    public CustomException(String message){
        super(message);
    }
}