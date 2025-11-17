package ai.anamaya.service.oms.config;

import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.InstantiatingGrpcChannelProvider;
import com.google.cloud.pubsub.v1.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.support.DefaultPublisherFactory;
import com.google.cloud.spring.pubsub.support.DefaultSubscriberFactory;

@Configuration
public class PubSubConfig {

    private static final String EMULATOR_HOST = "localhost:8085";
    private static final String PROJECT_ID = "gpn-endor-local";

    @Bean
    public DefaultPublisherFactory publisherFactory() {
        DefaultPublisherFactory factory = new DefaultPublisherFactory(() -> PROJECT_ID);
        factory.setChannelProvider(
            InstantiatingGrpcChannelProvider.newBuilder()
                .setEndpoint(EMULATOR_HOST)
                .build()
        );
        factory.setCredentialsProvider(NoCredentialsProvider.create());
        return factory;
    }

    @Bean
    public DefaultSubscriberFactory subscriberFactory() {
        DefaultSubscriberFactory factory = new DefaultSubscriberFactory(() -> PROJECT_ID);
        factory.setChannelProvider(
            InstantiatingGrpcChannelProvider.newBuilder()
                .setEndpoint(EMULATOR_HOST)
                .build()
        );
        factory.setCredentialsProvider(NoCredentialsProvider.create());
        return factory;
    }

    @Bean
    public PubSubTemplate pubSubTemplate(DefaultPublisherFactory publisherFactory,
                                         DefaultSubscriberFactory subscriberFactory) {
        return new PubSubTemplate(publisherFactory, subscriberFactory);
    }
}
