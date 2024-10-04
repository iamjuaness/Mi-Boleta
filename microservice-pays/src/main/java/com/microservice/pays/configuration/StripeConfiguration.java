package com.microservice.pays.configuration;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfiguration {

    @Value("${security.tokenStripe.tokenSecret}")
    private String tokenSecret;

    @PostConstruct
    public void init() {
        Stripe.apiKey = tokenSecret;
    }

}
