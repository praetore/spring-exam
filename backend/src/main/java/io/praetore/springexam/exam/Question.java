package io.praetore.springexam.exam;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

public record Question(
        String id,
        String topic,
        int difficulty,
        int elo,
        String type,
        String prompt,
        String code,
        List<Choice> choices,
        @JsonDeserialize(using = AnswerDeserializer.class) List<String> answer,
        String explanation,
        String trap
) {
    public record Choice(String id, String text) {
    }

    public QuestionView toView() {
        return new QuestionView(id, topic, difficulty, elo, type, prompt, code, choices);
    }
}
