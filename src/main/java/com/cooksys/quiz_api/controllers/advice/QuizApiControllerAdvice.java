package com.cooksys.quiz_api.controllers.advice;




import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.cooksys.quiz_api.dtos.ErrorDto;
import com.cooksys.quiz_api.exceptions.BadRequestException;
import com.cooksys.quiz_api.exceptions.NotFoundException;

@ControllerAdvice (basePackages = {"com.cooksys.quiz_api.controllers"})
@ResponseBody
public class QuizApiControllerAdvice {

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(BadRequestException.class)	
	public ErrorDto handleBadRequestException(HttpServletRequest request, BadRequestException exception) {
		return new ErrorDto("Bad request exception: " + exception.getMessage());
		
	}
	
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(NotFoundException.class)	
	public ErrorDto handleNotFoundException(HttpServletRequest request, NotFoundException exception) {
		return new ErrorDto("NotFoundException: " + exception.getMessage());
		
	}

}
