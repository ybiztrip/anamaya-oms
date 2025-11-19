package ai.anamaya.service.oms.core.config;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.messaging.MessageChannel;

@Configuration
public class PubSubConsumerConfig {

    @Bean
    public MessageChannel bookingStatusInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer bookingStatusInboundAdapter(PubSubTemplate pubSubTemplate) {

        PubSubInboundChannelAdapter adapter =
            new PubSubInboundChannelAdapter(pubSubTemplate, "oms-booking-status-subscriber");

        adapter.setOutputChannel(bookingStatusInputChannel());
        return adapter;
    }
}
