package com.newland.tianyan.gateway.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

/**
 * @Author: huangJunJie  2021-03-04 13:54
 */

@AllArgsConstructor
@Configuration
@EnableWebFluxSecurity
public class ResourceServerConfig {

    private final CheckTokenRestAuthenticationEntryPoint checkTokenRestAuthenticationEntryPoint;

    private final NoTokenRestAuthenticationEntryPoint noTokenRestAuthenticationEntryPoint;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.oauth2ResourceServer().jwt()
                .jwtAuthenticationConverter((Converter<Jwt, ? extends Mono<? extends AbstractAuthenticationToken>>) jwtAuthenticationConverter());

        http.oauth2ResourceServer().authenticationEntryPoint(checkTokenRestAuthenticationEntryPoint);
        http.authorizeExchange().anyExchange().authenticated();
        http.exceptionHandling().authenticationEntryPoint(noTokenRestAuthenticationEntryPoint);
        http.csrf().disable();
        return http.build();
    }

    @Bean
    public Converter<Jwt, ? extends Mono<? extends AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }
}
