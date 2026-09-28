package io.praetore.springexam.exam;

import java.util.List;

/** Public question payload without correct answers. */
public record QuestionView(
        String id,
        String topic,
        int difficulty,
        int elo,
        String type,
        String prompt,
        String code,
        List<Question.Choice> choices
) {
}
