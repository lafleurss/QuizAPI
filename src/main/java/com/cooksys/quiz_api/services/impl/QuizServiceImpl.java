package com.cooksys.quiz_api.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.cooksys.quiz_api.dtos.AnswerRequestDto;
import com.cooksys.quiz_api.dtos.QuestionRequestDto;
import com.cooksys.quiz_api.dtos.QuestionResponseDto;
import com.cooksys.quiz_api.dtos.QuizRequestDto;
import com.cooksys.quiz_api.dtos.QuizResponseDto;
import com.cooksys.quiz_api.entities.Answer;
import com.cooksys.quiz_api.entities.Question;
import com.cooksys.quiz_api.entities.Quiz;
import com.cooksys.quiz_api.exceptions.BadRequestException;
import com.cooksys.quiz_api.exceptions.NotFoundException;
import com.cooksys.quiz_api.mappers.AnswerMapper;
import com.cooksys.quiz_api.mappers.QuestionMapper;
import com.cooksys.quiz_api.mappers.QuizMapper;
import com.cooksys.quiz_api.repositories.AnswerRepository;
import com.cooksys.quiz_api.repositories.QuestionRepository;
import com.cooksys.quiz_api.repositories.QuizRepository;
import com.cooksys.quiz_api.services.QuizService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

	private final QuizRepository quizRepository;
	private final QuestionRepository questionRepository;
	private final AnswerRepository answerRepository;

	private final QuizMapper quizMapper;
	private final QuestionMapper questionMapper;
	private final AnswerMapper answerMapper;

	private void validateQuizRequest(QuizRequestDto quizRequestDto) {
		if (quizRequestDto.getName() == null || quizRequestDto.getName().isBlank()) {
			throw new BadRequestException("Bad Request. Quiz Name is required");
		}
	}

	private Optional<Quiz> getQuiz(Long id) {
		Optional<Quiz> quiz = quizRepository.findByIdAndDeletedFalse(id);
		return quiz;
	}

	private Optional<Question> getQuestionById(Long id) {
		Optional<Question> question = questionRepository.findById(id);
		return question;
	}

	@Override
	public List<QuizResponseDto> getAllQuizzes() {
		return quizMapper.quizEntitiesToDtos(quizRepository.findAllByDeletedFalse());
	}

	@Override
	public QuizResponseDto createQuiz(QuizRequestDto quizReqDto) {
		validateQuizRequest(quizReqDto);
		// Quiz
		Quiz quizToBeSaved = quizMapper.quizRequestDtoToEntity(quizReqDto);
		quizToBeSaved.setDeleted(false);
		quizToBeSaved.setQuestions(questionMapper.questionRequestDtoToEntities(quizReqDto.getQuestions()));
		Quiz savedQuiz = quizRepository.saveAndFlush(quizToBeSaved);

		// Check if questions were passed in the bodyquestionRequestDtoToEntities
		List<QuestionRequestDto> questionRequestDtoList = quizReqDto.getQuestions();
		if (questionRequestDtoList != null) {
			// For each Question
			List<Question> listOfQuestions = new ArrayList<>();
			for (QuestionRequestDto questionRequestDto : questionRequestDtoList) {
				Question questionToBeSaved = questionMapper.questionRequestDtoToEntity(questionRequestDto);

				// Link and Save Question
				questionToBeSaved.setAnswers(answerMapper.answerRequestDtosToEntities(questionRequestDto.getAnswers()));
				questionToBeSaved.setQuiz(savedQuiz);

				Question savedQuestion = questionRepository.saveAndFlush(questionToBeSaved);
				listOfQuestions.add(savedQuestion);

				// For each Answer in the question
				List<Answer> listOfAnswers = new ArrayList<>();
				List<AnswerRequestDto> answerRequestDtoList = questionRequestDto.getAnswers();

				for (AnswerRequestDto answerRequestDto : answerRequestDtoList) {
					Answer answerToBeSaved = answerMapper.answerRequestDtoToEntity(answerRequestDto);
					// Link answer to question saved
					answerToBeSaved.setQuestion(savedQuestion);
					Answer savedAnswer = answerRepository.saveAndFlush(answerToBeSaved);
					listOfAnswers.add(savedAnswer);
				}
				savedQuestion.setAnswers(listOfAnswers);
			}
			savedQuiz.setQuestions(listOfQuestions);

		}

		return quizMapper.quizEntityToDto(savedQuiz);
	}

	@Override
	public QuizResponseDto deleteQuiz(Long id) {
		Optional<Quiz> quizToDelete = getQuiz(id);
		if (quizToDelete.isPresent()) {
			quizToDelete.get().setDeleted(true);
			return quizMapper.quizEntityToDto(quizRepository.saveAndFlush(quizToDelete.get()));
		} else {
			throw new NotFoundException("Quiz with id: " + id + " not found!");
		}
	}

	@Override
	public QuestionResponseDto getRandomQsFromQuiz(Long id) {
		Optional<Quiz> quiz = getQuiz(id);

		if (quiz.isPresent()) {
			List<Question> possibleQuestions = quiz.get().getQuestions();
			int numOfQuestions = quiz.get().getQuestions().size();
			System.out.println(numOfQuestions);
			int rnd = new Random().nextInt(numOfQuestions);
			System.out.println(rnd);
			Long randomId = possibleQuestions.get(rnd).getId();
			System.out.println(randomId);
			return questionMapper.questionEntityToDto(getQuestionById(randomId).get());
		} else {
			throw new NotFoundException("Quiz with id: " + id + " not found!");
		}
	}

	@Override
	public QuizResponseDto renameQuiz(Long id, String newName) {
		Optional<Quiz> quiz = getQuiz(id);
		if (quiz.isPresent()) {
			Quiz quizToBeSaved = quiz.get();
			quizToBeSaved.setName(newName);
			return quizMapper.quizEntityToDto(quizRepository.saveAndFlush(quizToBeSaved));
		} else {
			throw new NotFoundException("Quiz with id: " + id + " not found!");
		}
	}

	@Override
	public QuizResponseDto addQsToQuiz(Long id, QuestionRequestDto questionRequestDto) {
		Optional<Quiz> quiz = getQuiz(id);

		if (quiz.isPresent()) {
			// Get Quiz from DB and its current list of questions
			Quiz quizToBeSaved = quiz.get();
			List<Question> currentQuestions = quizToBeSaved.getQuestions();

			// Question and Answers from Request
			Question questionToBeSaved = questionMapper.questionRequestDtoToEntity(questionRequestDto);
			List<AnswerRequestDto> answerRequestDtos = questionRequestDto.getAnswers();

			// Link answers to Question and Save Question
			questionToBeSaved.setAnswers(answerMapper.answerRequestDtosToEntities(answerRequestDtos));
			questionToBeSaved.setQuiz(quizToBeSaved);
			Question savedQuestion = questionRepository.saveAndFlush(questionToBeSaved);

			// For each Answer in the question
			List<Answer> answerList = new ArrayList<>();
			for (AnswerRequestDto answerRequestDto : answerRequestDtos) {
				Answer answerToBeSaved = answerMapper.answerRequestDtoToEntity(answerRequestDto);
				// Link answer to question saved
				answerToBeSaved.setQuestion(savedQuestion);
				Answer savedAnswer = answerRepository.saveAndFlush(answerToBeSaved);
				answerList.add(savedAnswer);
			}
			// Add new qs to the current List of qs. Link and Save Quiz
			currentQuestions.add(savedQuestion);
			savedQuestion.setAnswers(answerList);
			quizToBeSaved.setQuestions(currentQuestions);
			Quiz savedQuiz = quizRepository.saveAndFlush(quizToBeSaved);
			return quizMapper.quizEntityToDto(savedQuiz);
		} else {
			throw new NotFoundException("Quiz with id: " + id + " not found!");
		}
	}

	@Override
	public QuizResponseDto deleteQsFromQuiz(Long id, Long questionID) {
		Optional<Quiz> quiz = getQuiz(id);

		if (quiz.isPresent()) {
			// Get Quiz from DB and its current list of questions
			Quiz quizToBeSaved = quiz.get();
			List<Question> currentQuestions = quizToBeSaved.getQuestions();

			// Question from Request
			Optional<Question> questionToBeRemoved = getQuestionById(questionID);
			if (questionToBeRemoved.isPresent()) {
				// For each Answer in the question
				List<Answer> answerList = new ArrayList<>();
				for (Answer answerToBeRemoved : questionToBeRemoved.get().getAnswers()) {
					// Delete each answer in question to be removed
					answerRepository.delete(answerToBeRemoved);
				}

				// Remove qs from the current List of qs. Link and Save Quiz
				questionRepository.delete(questionToBeRemoved.get());
				currentQuestions.remove(questionToBeRemoved.get());
				quizToBeSaved.setQuestions(currentQuestions);

				Quiz savedQuiz = quizRepository.saveAndFlush(quizToBeSaved);
				return quizMapper.quizEntityToDto(savedQuiz);
			} else {
				throw new NotFoundException("Question with id: " + questionID + " not found in quiz with id: !" + id);
			}
		} else {
			throw new NotFoundException("Quiz with id: " + id + " not found!");
		}
	}

}
