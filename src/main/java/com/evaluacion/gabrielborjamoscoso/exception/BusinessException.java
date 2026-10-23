package com.evaluacion.gabrielborjamoscoso.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final int statusCode;

    public BusinessException(String message) {
        super(message);
        this.statusCode = 422; // Unprocessable Entity default for business rules
    }

    public BusinessException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
