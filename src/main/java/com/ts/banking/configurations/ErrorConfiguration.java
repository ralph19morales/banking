package com.ts.banking.configurations;

import com.ts.banking.controllers.ResponseWrapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ErrorConfiguration extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { RuntimeException.class })
    protected ResponseEntity<Object> handleNullPointerRuntimeException(RuntimeException ex, ServletWebRequest request) {
        return handleExceptionInternal(ex, ResponseWrapper.builder().data(new String(ex.getMessage())).build(),
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}
