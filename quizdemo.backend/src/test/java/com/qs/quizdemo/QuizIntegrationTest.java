package com.qs.quizdemo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.qs.quizdemo.model.QuestionModel;
import com.qs.quizdemo.repository.QuestionRepository;

import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class QuizIntegrationTest {

  @MockBean
  private QuestionRepository qr;

  @Autowired
  private MockMvc mockMvc;

  @Test
  @DisplayName("GET /question success")
  void testGetQuestion() throws Exception {
    // Setup our mocked service
    List<QuestionModel> questions = Arrays.asList(
        new QuestionModel("Question 1", null, null, null, new String[] { "Answer 1", "Answer 2" }, "Answer 1"),
        new QuestionModel("Question 2", null, null, null, new String[] { "Answer 1", "Answer 2" }, "Answer 2"));
    doReturn(questions).when(qr).getQuestions();

    // Execute the GET request
    mockMvc.perform(get("/questions"))
        // Validate the response code and content type
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        // Validate the returned fields
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].category", is(questions.get(0).getCategory())));
  }

  @Test
  @DisplayName("POST /answers success")
  void testCheckAnswer() throws Exception {

    // Prepare Questions and Answers
    var possibleAnswers = new String[] { "Answer 1", "Answer 2" };
    List<QuestionModel> questions = Arrays.asList(
        new QuestionModel("Question 1", null, null, null, possibleAnswers, "Answer 1"),
        new QuestionModel("Question 2", null, null, null, possibleAnswers, "Answer 2"));

    // Session attributes
    HashMap<String, Object> sessionattr = new HashMap<String, Object>();
    sessionattr.put("questions", questions);

    // Convert answers to JSON
    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    String json = ow.writeValueAsString(possibleAnswers);

    // Execute the POST request
    mockMvc.perform(post("/answers").sessionAttrs(sessionattr).content(json).header("Content-Type", "application/json"))
        // Validate the response code and content type
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        // Validate the returned fields
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0]", is(true)))
        .andExpect(jsonPath("$[1]", is(true)));
  }

  @Test
  @DisplayName("POST /answers failure (no session)")
  void testCheckAnswerNoSession() throws Exception {

    // Prepare Questions and Answers
    var possibleAnswers = new String[] { "Answer 1", "Answer 2" };

    // Convert answers to JSON
    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    String json = ow.writeValueAsString(possibleAnswers);

    // Execute the POST request
    mockMvc.perform(post("/answers").content(json).header("Content-Type", "application/json"))
        // Validate the response code and content type
        .andExpect(status().is(428));
  }

  @Test
  @DisplayName("POST /answers bad request")
  void testCheckAnswerBadRequest() throws Exception {

    // Prepare Questions and Answers
    var possibleAnswers = new String[] { "Answer 1", "Answer 2" };
    List<QuestionModel> questions = Arrays.asList(
        new QuestionModel("Question 1", null, null, null, possibleAnswers, "Answer 1"),
        new QuestionModel("Question 2", null, null, null, possibleAnswers, "Answer 2"));

    // Session attributes
    HashMap<String, Object> sessionattr = new HashMap<String, Object>();
    sessionattr.put("questions", questions);

    // Execute the POST request
    mockMvc.perform(post("/answers").sessionAttrs(sessionattr).header("Content-Type", "application/json"))
        // Validate the response code and content type
        .andExpect(status().isBadRequest());
  }

}