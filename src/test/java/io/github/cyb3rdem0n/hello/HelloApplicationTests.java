package io.github.cyb3rdem0n.hello;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureObservability   // keep the Prometheus endpoint active in tests
class HelloApplicationTests {

    @Autowired
    MockMvc mvc;

    @Test
    void helloReturnsGreeting() throws Exception {
        mvc.perform(get("/hello").param("name", "Tundr"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hello, Tundr!"));
    }

    @Test
    void failReturns500() throws Exception {
        mvc.perform(get("/fail")).andExpect(status().isInternalServerError());
    }

    @Test
    void probesAreUp() throws Exception {
        mvc.perform(get("/actuator/health/readiness")).andExpect(status().isOk());
        mvc.perform(get("/actuator/health/liveness")).andExpect(status().isOk());
    }

    @Test
    void prometheusEndpointExposesBusinessMetric() throws Exception {
        mvc.perform(get("/hello"));
        mvc.perform(get("/actuator/prometheus"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("hello_greetings_total")));
    }
}
