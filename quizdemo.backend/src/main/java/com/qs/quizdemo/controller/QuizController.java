package com.qs.quizdemo.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.qs.quizdemo.model.QuestionModel;
import com.qs.quizdemo.repository.QuestionRepository;
import com.qs.quizdemo.service.QuestionService;

import jakarta.servlet.http.HttpSession;

@RestController
public class QuizController {

    @Autowired
    private QuestionRepository qr;

    @Autowired
    private QuestionService qs;

    @GetMapping("/questions")
    @SuppressWarnings({ "unchecked" })
    List<QuestionModel> getQuestions(HttpSession session) throws IOException {
        List<QuestionModel> questions = (List<QuestionModel>) session.getAttribute("questions");
        if (questions != null) {
            return questions;
        }

        questions = qr.getQuestions();
        questions.forEach(q -> qs.shuffleAnswers(q.getAnswers()));
        session.setAttribute("questions", questions);
        return questions;
    }

    // DEBUG METHOD

    // @GetMapping("/answers")
    // String[] getAnswers(HttpSession httpSession) {
    // return getCorrectAnswersFromSession(httpSession);
    // }

    @PostMapping("/answers")
    ResponseEntity<?> checkAnswers(HttpSession session, @RequestBody String[] givenAnswers) {
        String[] correctAnswers = getCorrectAnswersFromSession(session);
        session.removeAttribute("questions");
        if (correctAnswers == null)
            return ResponseEntity.status(428).body(null);
        Boolean[] correctGivenAnswers = qs.checkAnswers(givenAnswers, correctAnswers);
        if (correctGivenAnswers.length < correctAnswers.length)
            return ResponseEntity.status(400).body(null);
        return ResponseEntity.status(200).body(correctGivenAnswers);
    }

    String[] getCorrectAnswersFromSession(HttpSession session) {
        List<?> questions = (List<?>) session.getAttribute("questions");
        if (questions == null)
            return null;
        return questions.stream()
                .map(q -> ((QuestionModel) q).getCorrectAnswer())
                .toArray(String[]::new);
    }

}
