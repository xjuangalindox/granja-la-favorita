package com.example.demo.advice;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ConejoExceptionHandler {

    // Error mostrado en PostMan
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> messageException(MethodArgumentNotValidException error){
        Map<String, String> mapa = new HashMap<>();

        // Lista.forEach - Lambda
        error.getBindingResult().getFieldErrors().forEach(x -> {
            mapa.put(x.getField(), x.getDefaultMessage());
        });

        return mapa;
    }

    /*
    @ExceptionHandler(DataIntegrityViolationException.class)
    public Map<String, String> handleDataIntegrityViolationException(DataIntegrityViolationException error){
        Map<String, String> mapa = new HashMap<>();

        //mapa.put("error", error.getMostSpecificCause().toString());
        mapa.put("message", "El nombre debe ser único e irrepetible");

        return mapa;
    }*/
}
