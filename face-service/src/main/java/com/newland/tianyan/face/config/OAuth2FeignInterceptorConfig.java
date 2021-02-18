package com.newland.tianyan.face.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.security.oauth2.client.feign.OAuth2FeignRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;

@Configuration
public class OAuth2FeignInterceptorConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new OAuth2FeignRequestInterceptor(new DefaultOAuth2ClientContext(), resourceDetails());
    }

    @Value("${auth-service.host}")
    private String host;

    @Value("${auth-service.port}")
    private String port;

    @Value("${auth-service.client-id}")
    private String clientId;

    @Value("${auth-service.client-secret}")
    private String clientSecret;

    private OAuth2ProtectedResourceDetails resourceDetails() {
        final ClientCredentialsResourceDetails details = new ClientCredentialsResourceDetails();
        String url = "http://"+host+":"+port+"/oauth/token";
        details.setAccessTokenUri(url);
        details.setClientId(clientId);
        details.setClientSecret(clientSecret);
        return details;
    }
}
