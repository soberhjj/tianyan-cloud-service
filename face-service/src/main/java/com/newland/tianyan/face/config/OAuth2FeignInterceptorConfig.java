package com.newland.tianyan.face.config;

import feign.RequestInterceptor;
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

    private OAuth2ProtectedResourceDetails resourceDetails() {
        final ClientCredentialsResourceDetails details = new ClientCredentialsResourceDetails();
        //TODO: remove magic number
        details.setAccessTokenUri("http://localhost:18081/oauth/token");
//        details.setAccessTokenUri("http://192.168.136.34:18081/oauth/token"); //这里的IP和端口是auth-cloud服务的IP和端口
        details.setClientId("client_2");
        details.setClientSecret("123456");
        return details;
    }
}
