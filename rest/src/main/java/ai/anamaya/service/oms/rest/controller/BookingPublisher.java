package ai.anamaya.service.oms.rest.controller;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookingPublisher {

    private final PubSubTemplate pubSubTemplate;

    public BookingPublisher(PubSubTemplate pubSubTemplate) {
        this.pubSubTemplate = pubSubTemplate;
    }

    @PostMapping("/orders")
    public String publishOrder() {
        pubSubTemplate.publish("oms-booking-status", "sabriyan");
        return "Order published successfully!";
    }
}