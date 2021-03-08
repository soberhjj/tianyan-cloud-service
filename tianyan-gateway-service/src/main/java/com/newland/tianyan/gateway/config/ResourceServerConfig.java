package com.newland.tianyan.gateway.config;

import cn.hutool.core.util.ArrayUtil;
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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        // jwt 增加
        http.oauth2ResourceServer().jwt()
                .jwtAuthenticationConverter(jwtAuthenticationConverter());
        //自定义处理JWT请求头过期或签名错误的结果
        http.oauth2ResourceServer().authenticationEntryPoint(checkTokenRestAuthenticationEntryPoint);
        http.exceptionHandling().authenticationEntryPoint(noTokenRestAuthenticationEntryPoint);
        http.csrf().disable();
        // 白名单
        String whileList = "/cloudService/auth-cloud/oauth/token";
        List<String> whileLists = Arrays.stream(whileList.split(",")).collect(Collectors.toList());
        http.authorizeExchange()
                .pathMatchers(ArrayUtil.toArray(whileLists, String.class)).permitAll();
        http.authorizeExchange().anyExchange().authenticated();
        return http.build();
    }

    @Bean
    public Converter<Jwt, ? extends Mono<? extends AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }

}
