package io.praetore.springexam.exam;

import java.util.List;

public record AnswerResult(
        boolean correct,
        List<String> expected,
        String explanation,
        String trap,
        int playerElo,
        int questionElo,
        int eloDelta
) {
}
