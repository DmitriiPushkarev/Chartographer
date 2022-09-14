package com.pushkarev.chartographer.service.exceptions;

public class ChartaInvalidCoordinateException extends Exception {
	
	public ChartaInvalidCoordinateException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
	
	public ChartaInvalidCoordinateException(String errorMessage) {
        super(errorMessage);
    }
}
