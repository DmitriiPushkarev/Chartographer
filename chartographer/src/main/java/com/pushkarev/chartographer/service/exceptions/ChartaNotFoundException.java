package com.pushkarev.chartographer.service.exceptions;

public class ChartaNotFoundException extends Exception {
	
	public ChartaNotFoundException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
	
	public ChartaNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
