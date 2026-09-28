package io.praetore.springexam.exam;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record AnswerRequest(
        @NotBlank String questionId,
        @NotEmpty List<@NotBlank String> choiceIds
) {
}
