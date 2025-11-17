package ai.anamaya.service.oms.config;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.AckMode;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.messaging.MessageChannel;

@Configuration
public class PubSubConfig {

    @Bean
    public MessageChannel inputMessageChannel() {
        return new PublishSubscribeChannel();
    }

    @Bean
    public PubSubInboundChannelAdapter inboundChannelAdapter(
        @Qualifier("inputMessageChannel") MessageChannel messageChannel,
        PubSubTemplate pubSubTemplate) {
        PubSubInboundChannelAdapter adapter =
            new PubSubInboundChannelAdapter(pubSubTemplate, "projects/gpn-endor-local/subscriptions/oms-booking-status");
        adapter.setOutputChannel(messageChannel);
        adapter.setAckMode(AckMode.AUTO);
        adapter.setPayloadType(String.class);
        return adapter;
    }

//    @Bean
//    @InboundChannelAdapter(value = "inputMessageChannel", poller = @Poller(fixedDelay = "1000", maxMessagesPerPoll = "5"))
//    public MessageSource<Object> synchronousPubSubMessageSource(PubSubTemplate pubSubTemplate) {
//        PubSubMessageSource messageSource = new PubSubMessageSource(pubSubTemplate, "projects/is-chibuisi-dev/subscriptions/emailsubscription");
//        messageSource.setAckMode(AckMode.AUTO);
//        messageSource.setPayloadType(String.class);
//        messageSource.setMaxFetchSize(5);
//        return messageSource;
//    }


}
