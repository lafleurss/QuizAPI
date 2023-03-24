package com.cooksys.quiz_api.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class AnswerRequestDto {
	private Long id;
	
	private String text;	
	
	private QuestionRequestDto questionRequestDto;

}
