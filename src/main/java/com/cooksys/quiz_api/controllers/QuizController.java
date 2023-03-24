package com.cooksys.quiz_api.controllers;

import java.util.List;

import com.cooksys.quiz_api.dtos.QuestionRequestDto;
import com.cooksys.quiz_api.dtos.QuestionResponseDto;
import com.cooksys.quiz_api.dtos.QuizRequestDto;
import com.cooksys.quiz_api.dtos.QuizResponseDto;
import com.cooksys.quiz_api.services.QuizService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/quiz")
public class QuizController {

	private final QuizService quizService;
	private QuizService questionService;

	@GetMapping
	public List<QuizResponseDto> getAllQuizzes() {
		return quizService.getAllQuizzes();
	}

	@GetMapping("/{id}/random")
	public QuestionResponseDto getRandomQsFromQuiz(@PathVariable Long id) {
		return quizService.getRandomQsFromQuiz(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public QuizResponseDto createQuiz(@RequestBody QuizRequestDto quizRequestDto) {
		return quizService.createQuiz(quizRequestDto);
	}
	
	@PatchMapping("/{id}/rename/{newName}")	
	public QuizResponseDto renameQuiz(@PathVariable Long id, @PathVariable String newName) {
		return quizService.renameQuiz(id, newName);
	}
	
	@PatchMapping("/{id}/add")	
	public QuizResponseDto addQsToQuiz(@PathVariable Long id, @RequestBody QuestionRequestDto questionRequestDto) {
		return quizService.addQsToQuiz(id, questionRequestDto);
	}

	@DeleteMapping("/{id}")
	public QuizResponseDto deleteQuiz(@PathVariable Long id) {
		return quizService.deleteQuiz(id);
	}
	
	@DeleteMapping("/{id}/delete/{questionID}")
	public QuizResponseDto deleteQsFromQuiz(@PathVariable Long id, @PathVariable Long questionId) {
		return quizService.deleteQsFromQuiz(id);
	}

	// TODO: Implement the remaining 6 endpoints from the documentation.

}
