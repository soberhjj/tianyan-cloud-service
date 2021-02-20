//package com.newland.tianyan.face.config;
//
//import feign.RequestInterceptor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cloud.security.oauth2.client.feign.OAuth2FeignRequestInterceptor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
//import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
//import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
//
//@Configuration
//public class OAuth2FeignInterceptorConfig {
//
//    @Bean
//    public RequestInterceptor requestInterceptor() {
//        return new OAuth2FeignRequestInterceptor(new DefaultOAuth2ClientContext(), resourceDetails());
//    }
//
//    @Value("${auth-service.client-id}")
//    private String clientId;
//
//    @Value("${auth-service.client-secret}")
//    private String clientSecret;
//
//    private OAuth2ProtectedResourceDetails resourceDetails() {
//        final ClientCredentialsResourceDetails details = new ClientCredentialsResourceDetails();
//        String url = "http://auth-service/oauth/token";
//        details.setAccessTokenUri(url);
//        details.setClientId(clientId);
//        details.setClientSecret(clientSecret);
//        return details;
//    }
//}

//auth-service:
//        client-id: client_2
//        client-secret: 123456