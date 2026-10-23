package io.github.cyb3rdem0n.hello;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
public class HelloController {

    private final Counter greetings;
    private final String version;

    public HelloController(MeterRegistry registry,
                           @Value("${app.version:dev}") String version) {
        // business metric: shows up in Prometheus as hello_greetings_total
        this.greetings = Counter.builder("hello.greetings")
                .description("Greetings served")
                .register(registry);
        this.version = version;
    }

    @GetMapping("/hello")
    public Map<String, String> hello(@RequestParam(defaultValue = "world") String name) {
        greetings.increment();
        return Map.of("message", "Hello, " + name + "!", "version", version);
    }

    /**
     * Always fails with HTTP 500. Used in the lab to generate errors on purpose
     * and watch alerts and SLO burn rate react.
     */
    @GetMapping("/fail")
    public Map<String, String> fail() {
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Simulated failure");
    }
}
