package com.pushkarev.chartographer.service.exceptions;

public class InvalidContentTypeException extends Exception{
	
	public InvalidContentTypeException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
	
	public InvalidContentTypeException(String errorMessage) {
        super(errorMessage);
    }
}
