package com.Alcura.Customer.Controller;

import com.Alcura.Customer.Service.LlamaService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/chat")
public class LlamaController
{
    private final LlamaService llamaService;

    public LlamaController(LlamaService llamaService)
    {
        this.llamaService = llamaService;
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamResponse(@RequestParam String prompt)
    {
        return llamaService.generateStream(prompt)
                .onErrorResume(e -> Flux.just("[Error: " + e.getMessage() + " ]"));
    }
}
