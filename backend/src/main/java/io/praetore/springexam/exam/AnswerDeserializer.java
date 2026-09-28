package io.praetore.springexam.exam;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Accepts answer as a single string or an array of strings (JSON Schema oneOf).
 */
public class AnswerDeserializer extends JsonDeserializer<List<String>> {

    @Override
    public List<String> deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        List<String> answers = new ArrayList<>();
        if (node.isArray()) {
            node.forEach(item -> answers.add(item.asText()));
        } else if (node.isTextual()) {
            answers.add(node.asText());
        } else {
            throw context.weirdStringException(node.toString(), List.class, "answer must be string or string[]");
        }
        return List.copyOf(answers);
    }
}
