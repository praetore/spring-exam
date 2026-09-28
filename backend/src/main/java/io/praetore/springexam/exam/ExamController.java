package io.praetore.springexam.exam;

import jakarta.validation.Valid;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class ExamController {

    private final ExamService examService;

    public ExamController(ExamService examService) {
        this.examService = examService;
    }

    @GetMapping("/elo")
    public EloStatus elo() {
        return examService.status();
    }

    @PostMapping("/elo/reset")
    public EloStatus reset() {
        examService.reset();
        return examService.status();
    }

    @GetMapping("/topics")
    public List<String> topics() {
        return examService.topics();
    }

    @GetMapping("/questions/next")
    public QuestionView next(@RequestParam(required = false) String topic) {
        return examService.next(topic);
    }

    @PostMapping("/answers")
    public AnswerResult answer(@Valid @RequestBody AnswerRequest request) {
        return examService.submit(request);
    }

    @GetMapping(value = "/schema", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Resource> schema() {
        return ResponseEntity.ok(new ClassPathResource("schema/exam-question.schema.json"));
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "ok");
    }
}
