package com.Alcura.Customer.Service;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class LlamaService
{
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final AtomicReference<String> lasttoken = new AtomicReference<>("");

    public LlamaService(@Qualifier("llamaWebClient") WebClient webClient, ObjectMapper objectMapper)
    {
        this.webClient = webClient;
        this.objectMapper = new ObjectMapper();
    }

    public Flux<String> generateStream(String prompt)
    {
        lasttoken.set("");

        String body = String.format("{\"model\":\"llama3\", \"prompt\":\"%s\", \"stream\":true}", prompt);

        return  webClient.post()
                .uri("/api/generate")
                .bodyValue(body)
                .retrieve()
                .bodyToFlux(String.class)
                .map(this::extractResponseText)
                .filter(text -> text != null && !text.isEmpty())
                .map(this::processTokenWithSpacing)
                .delayElements(Duration.ofMillis(20))
                .doFinally(signal -> lasttoken.set(""));
    }

    private String extractResponseText(String jsonLine)
    {
        try
        {
            JsonNode node = objectMapper.readTree(jsonLine);
            return node.has("response") ? node.get("response").asText() : "";
        }
        catch (JsonProcessingException e)
        {
            return "";
        }
    }

    private String processTokenWithSpacing (String currentToken)
    {
        String previous = lasttoken.getAndSet(currentToken);

        if (previous.isEmpty())
        {
            return currentToken;
        }
        if (shouldAddSpace(previous,currentToken))
        {
            return " " + currentToken;
        }
        return currentToken;
    }

    private boolean shouldAddSpace (String previous, String current)
    {
        if (previous.matches(".*[\\s({\\['\"]$")) return false;

        if (current.matches("^[\\s.,!?;:')}\\]\"].*")) return false;

        return previous.matches(".*\\w$") && current.matches("^\\w.*");
    }
}
