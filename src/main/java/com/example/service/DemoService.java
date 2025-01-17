package com.example.service;



import com.example.model.Demo;
import com.example.repository.DemoRepository;
import com.example.kafka.KafkaProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ignite.IgniteCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;



@Service
public class DemoService {
    private static final Logger logger = LoggerFactory.getLogger(DemoService.class);

    private final DemoRepository demoRepository;
    private final KafkaProducer kafkaProducer;
    private final IgniteCache<String, Object> demoCache;
    private final ObjectMapper objectMapper;

    public DemoService(
            DemoRepository demoRepository,
            KafkaProducer kafkaProducer,
            IgniteCache<String, Object> demoCache,
            ObjectMapper objectMapper) {
        this.demoRepository = demoRepository;
        this.kafkaProducer = kafkaProducer;
        this.demoCache = demoCache;
        this.objectMapper = objectMapper;
    }

    public void createDemo(Demo demo) throws Exception {
        try {
            // Save to Cassandra
            demoRepository.save(demo);

            // Try to cache the demo object
            try {
                demoCache.put(demo.getId(), demo);
            } catch (Exception e) {
                logger.warn("Failed to cache demo object: {}", e.getMessage());
                // Continue execution even if caching fails
            }

            // Send to Kafka
            String message = objectMapper.writeValueAsString(demo);
            kafkaProducer.sendMessage("demo-topic", message);
        } catch (Exception e) {
            logger.error("Failed to create demo: {}", e.getMessage(), e);
            throw e;
        }
    }

    public Demo getDemo(String id) {
        Demo demo = null;

        // Try to get from cache first
        try {
            demo = (Demo) demoCache.get(id);
        } catch (Exception e) {
            logger.warn("Failed to retrieve from cache: {}", e.getMessage());
        }

        if (demo == null) {
            // If not in cache or cache failed, get from Cassandra
            demo = demoRepository.findById(id).orElse(null);

            if (demo != null) {
                // Try to put in cache
                try {
                    demoCache.put(id, demo);
                } catch (Exception e) {
                    logger.warn("Failed to update cache: {}", e.getMessage());
                }
            }
        }

        return demo;
    }
}