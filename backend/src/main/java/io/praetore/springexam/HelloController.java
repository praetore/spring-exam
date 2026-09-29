package io.praetore.springexam;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Minimal sandbox endpoint — extend this package when practicing Spring for the exam.
 * The ELO quiz lives entirely in {@code frontend/}.
 */
@RestController
public class HelloController {

    @GetMapping("/api/hello")
    public String hello() {
        return "Hello from Spring Exam!";
    }
}
