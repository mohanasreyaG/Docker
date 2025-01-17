package com.example.kafka;

import com.example.model.Demo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ignite.IgniteCache;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {
    private final IgniteCache<String, Object> demoCache;
    private final ObjectMapper objectMapper;

    public KafkaConsumer(IgniteCache<String, Object> demoCache, ObjectMapper objectMapper) {
        this.demoCache = demoCache;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "demo-topic")
    public void listen(String message) throws Exception {
        Demo demo = objectMapper.readValue(message, Demo.class);
        demoCache.put(demo.getId(), demo);
    }
}
