package com.qs.quizdemo.service;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.stereotype.Service;

@Service
public class QuestionService {

    public String[] shuffleAnswers(String[] answers) {
        Collections.shuffle(Arrays.asList(answers));
        return answers;
    }

    public Boolean[] checkAnswers(String[] givenAnswers, String[] correctAnswers) {
        Boolean[] correctGivenAnswers = new Boolean[correctAnswers.length];
        if (givenAnswers.length != correctAnswers.length)
            return new Boolean[0];
        for (int i = 0; i < correctAnswers.length; i++) {
            correctGivenAnswers[i] = givenAnswers[i].equals(correctAnswers[i]);
        }
        return correctGivenAnswers;
    }
}
