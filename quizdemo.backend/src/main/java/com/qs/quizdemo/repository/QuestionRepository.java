package com.qs.quizdemo.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qs.quizdemo.model.Difficulty;
import com.qs.quizdemo.model.QuestionModel;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class QuestionRepository {

    private final ObjectMapper objectMapper;

    @Value("${api.url}")
    private String API_URL;

    public QuestionRepository() {
        this.objectMapper = new ObjectMapper();
    }

    public List<QuestionModel> getQuestions() throws IOException {
        List<QuestionModel> questions = new ArrayList<>();

        JsonNode response = objectMapper.readTree(new URL(API_URL));
        JsonNode results = response.get("results");

        for (JsonNode result : results) {
            String category = result.get("category").asText();
            String type = result.get("type").asText();
            String difficulty = result.get("difficulty").asText();
            String question = result.get("question").asText();
            String[] answers = objectMapper.convertValue(result.get("incorrect_answers"), String[].class);
            String correctAnswer = result.get("correct_answer").asText();
            answers = mergeAnswers(answers, correctAnswer);
            questions.add(
                    new QuestionModel(category, type, Difficulty.valueOf(difficulty.toUpperCase()), question, answers,
                            correctAnswer));
        }
        return questions;
    }

    private String[] mergeAnswers(String[] answers, String correctAnswer) {
        String[] updatedAnswers = new String[answers.length + 1];
        System.arraycopy(answers, 0, updatedAnswers, 0, answers.length);
        updatedAnswers[answers.length] = correctAnswer;
        return updatedAnswers;
    }
}