package com.qs.quizdemo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonIgnoreProperties("correctAnswer")
@AllArgsConstructor
public class QuestionModel {
    @Getter
    private String category;
    @Getter
    private String type;
    @Getter
    private Difficulty difficulty;
    @Getter
    private String question;
    @Getter
    private String[] answers;
    @Getter
    private String correctAnswer;

}
