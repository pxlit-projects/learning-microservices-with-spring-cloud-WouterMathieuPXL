package be.pxl.services;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueueConfiguration {

    @Bean
    public Queue auditQueue() {
        return new Queue("auditQueue", false);
    }
}
