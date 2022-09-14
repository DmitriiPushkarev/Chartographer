package com.pushkarev.chartographer.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import com.pushkarev.chartographer.service.exceptions.ChartaInvalidCoordinateException;
import com.pushkarev.chartographer.service.exceptions.ChartaNotFoundException;
import com.pushkarev.chartographer.service.exceptions.InvalidContentTypeException;

@ControllerAdvice
public class ControllerExceptionHandler {

	  @ExceptionHandler(value = {javax.validation.ConstraintViolationException.class, java.lang.NumberFormatException.class})
	  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
	  public void validationException(RuntimeException ex, WebRequest request) {
	    
	  }
	  
	  @ExceptionHandler(ChartaNotFoundException.class)
	  @ResponseStatus(value = HttpStatus.NOT_FOUND)
	  public void notFoundChartaByIDException(ChartaNotFoundException ex, WebRequest request) {
	    
	  }
    
	  @ExceptionHandler(ChartaInvalidCoordinateException.class)
	  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
	  public void chartaInvalidCoordinateException(ChartaInvalidCoordinateException ex, WebRequest request) {
	    
	  }
	  
	  @ExceptionHandler(org.springframework.web.multipart.MultipartException.class)
	  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
	  public void emptyFileException(RuntimeException ex, WebRequest request) {
	    
	  }
	  
	  @ExceptionHandler(InvalidContentTypeException.class)
	  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
	  public void invalidContentTypeException(InvalidContentTypeException ex, WebRequest request) {
	    
	  }
}
