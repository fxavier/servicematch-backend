package com.xavier.servicematchbackend.identityaccess.infra.messaging;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "messaging")
public class MessagingProperties {

    private String exchange = "servicematch.events";

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }
}
