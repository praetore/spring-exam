package io.praetore.springexam.exam;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ExamControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void nextQuestionHidesAnswer() throws Exception {
        mockMvc.perform(get("/api/questions/next"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.choices").isArray())
                .andExpect(jsonPath("$.answer").doesNotExist());
    }

    @Test
    void submitUpdatesElo() throws Exception {
        String questionJson = mockMvc.perform(get("/api/questions/next"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode question = objectMapper.readTree(questionJson);
        String id = question.get("id").asText();

        mockMvc.perform(post("/api/answers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"questionId\":\"" + id + "\",\"choiceIds\":[\"a\"]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playerElo").value(greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.explanation").isNotEmpty());
    }

    @Test
    void schemaIsAvailable() throws Exception {
        mockMvc.perform(get("/api/schema"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("SpringExamQuestion"));
    }
}
