package io.praetore.springexam.exam;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class ExamService {

    private static final int K_FACTOR = 32;

    private final ObjectMapper objectMapper;
    private final PlayerStatsRepository playerStatsRepository;
    private final List<Question> questions = new ArrayList<>();
    private final Map<String, Question> byId = new ConcurrentHashMap<>();
    private final Map<String, Integer> questionElo = new ConcurrentHashMap<>();

    public ExamService(ObjectMapper objectMapper, PlayerStatsRepository playerStatsRepository) {
        this.objectMapper = objectMapper;
        this.playerStatsRepository = playerStatsRepository;
    }

    @PostConstruct
    void loadQuestions() throws IOException {
        JsonSchema schema = loadSchema();
        JsonNode root;
        try (InputStream in = new ClassPathResource("questions/questions.json").getInputStream()) {
            root = objectMapper.readTree(in);
        }
        if (!root.isArray()) {
            throw new IllegalStateException("questions.json must be a JSON array");
        }
        for (JsonNode node : root) {
            Set<ValidationMessage> errors = schema.validate(node);
            if (!errors.isEmpty()) {
                String details = errors.stream()
                        .map(ValidationMessage::getMessage)
                        .collect(Collectors.joining("; "));
                throw new IllegalStateException("Question failed schema validation: " + details);
            }
            Question question = objectMapper.treeToValue(node, Question.class);
            questions.add(question);
            byId.put(question.id(), question);
            questionElo.put(question.id(), question.elo());
        }
        if (questions.isEmpty()) {
            throw new IllegalStateException("No exam questions loaded");
        }
    }

    private JsonSchema loadSchema() throws IOException {
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
        try (InputStream in = new ClassPathResource("schema/exam-question.schema.json").getInputStream()) {
            JsonNode schemaNode = objectMapper.readTree(in);
            return factory.getSchema(schemaNode);
        }
    }

    private PlayerStats requirePlayer() {
        return playerStatsRepository.findById(PlayerStats.DEFAULT_PLAYER_ID)
                .orElseThrow(() -> new IllegalStateException("Player stats missing; expected ApplicationRunner seed"));
    }

    @Transactional(readOnly = true)
    public EloStatus status() {
        PlayerStats player = requirePlayer();
        return new EloStatus(player.getElo(), player.getAnswered(), player.getCorrectCount());
    }

    @Transactional
    public void reset() {
        PlayerStats player = requirePlayer();
        player.reset();
        playerStatsRepository.save(player);
        questions.forEach(q -> questionElo.put(q.id(), q.elo()));
    }

    public List<String> topics() {
        return questions.stream().map(Question::topic).distinct().sorted().toList();
    }

    @Transactional(readOnly = true)
    public QuestionView next(String topic) {
        int elo = requirePlayer().getElo();
        return questions.stream()
                .filter(q -> topic == null || topic.isBlank() || q.topic().equalsIgnoreCase(topic))
                .min(Comparator.comparingInt(q -> Math.abs(questionElo.getOrDefault(q.id(), q.elo()) - elo)))
                .map(Question::toView)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No question for topic"));
    }

    @Transactional
    public AnswerResult submit(AnswerRequest request) {
        Question question = byId.get(request.questionId());
        if (question == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown question");
        }

        Set<String> expected = new HashSet<>(question.answer());
        Set<String> given = request.choiceIds().stream()
                .map(id -> id.toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());
        Set<String> expectedNormalized = expected.stream()
                .map(id -> id.toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());
        boolean correct = given.equals(expectedNormalized);

        PlayerStats player = requirePlayer();
        int qElo = questionElo.getOrDefault(question.id(), question.elo());
        int pElo = player.getElo();
        int delta = eloDelta(pElo, qElo, correct);
        int newPlayerElo = pElo + delta;
        player.setElo(newPlayerElo);
        player.setAnswered(player.getAnswered() + 1);
        if (correct) {
            player.setCorrectCount(player.getCorrectCount() + 1);
        }
        playerStatsRepository.save(player);
        questionElo.put(question.id(), Math.max(0, qElo - delta));

        return new AnswerResult(
                correct,
                List.copyOf(question.answer()),
                question.explanation(),
                question.trap(),
                newPlayerElo,
                questionElo.get(question.id()),
                delta
        );
    }

    private static int eloDelta(int player, int question, boolean won) {
        double expected = 1.0 / (1.0 + Math.pow(10, (question - player) / 400.0));
        double score = won ? 1.0 : 0.0;
        return (int) Math.round(K_FACTOR * (score - expected));
    }
}
