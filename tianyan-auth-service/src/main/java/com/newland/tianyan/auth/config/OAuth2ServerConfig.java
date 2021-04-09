package com.newland.tianyan.auth.config;


import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import com.newland.tianyan.auth.filter.JsonToUrlEncodedAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.protobuf.ProtobufJsonFormatHttpMessageConverter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.expression.OAuth2WebSecurityExpressionHandler;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.Serializable;
import java.security.KeyPair;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class OAuth2ServerConfig {

    @Bean
    public ProtobufJsonFormatHttpMessageConverter protobufJsonFormatHttpMessageConverter() {

        JsonFormat.Printer printer = JsonFormat.printer().preservingProtoFieldNames();
        JsonFormat.Parser parser = JsonFormat.parser();
        return new ProtobufJsonFormatHttpMessageConverter(parser, printer) {

            @Override
            protected void addDefaultHeaders(HttpHeaders headers, Message message, MediaType contentType) throws IOException {
                super.addDefaultHeaders(headers, message, new MediaType("application", "json", DEFAULT_CHARSET));
            }
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Configuration
    @EnableResourceServer
    protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

        @Autowired
        private JsonToUrlEncodedAuthenticationFilter urlEncodedAuthenticationFilter;

        @Autowired
        private OAuth2WebSecurityExpressionHandler expressionHandler;

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) {
            resources.expressionHandler(expressionHandler);
        }

        @Bean
        public OAuth2WebSecurityExpressionHandler oAuth2WebSecurityExpressionHandler(ApplicationContext applicationContext) {
            OAuth2WebSecurityExpressionHandler expressionHandler = new OAuth2WebSecurityExpressionHandler();
            expressionHandler.setApplicationContext(applicationContext);
            return expressionHandler;
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
            // @formatter:off
            http
                .addFilterBefore(urlEncodedAuthenticationFilter, ChannelProcessingFilter.class)
                .authorizeRequests()
                    .antMatchers( "/actuator/**","/test",
                            "/auth/**","/login/register",
                            "/rsa/publicKey").permitAll()
                    .anyRequest().authenticated();
            // @formatter:on
        }
    }

    @Configuration
    @EnableAuthorizationServer
    protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

        private final AuthenticationManager authenticationManager;
        private final DataSource dataSource;

        public AuthorizationServerConfiguration(AuthenticationManager authenticationManager, DataSource dataSource) {
            this.authenticationManager = authenticationManager;
            this.dataSource = dataSource;
        }

        @Bean
        public JdbcClientDetailsService jdbcClientDetailsService(DataSource dataSource) {
            return new JdbcClientDetailsService(dataSource);
        }

        @Bean
        public OAuth2RequestFactory oAuth2RequestFactory() {
            return new DefaultOAuth2RequestFactory(jdbcClientDetailsService(dataSource)) {
                @Override
                public OAuth2Request createOAuth2Request(ClientDetails client, TokenRequest tokenRequest) {
                    OAuth2Request request = super.createOAuth2Request(client, tokenRequest);

                    // 把 oauth_client_details 中的 additional_information 的值放入 extensions
                    Map<String, Object> additionalInformation = client.getAdditionalInformation();
                    for (String key : additionalInformation.keySet()) {
                        request.getExtensions().put(key, (Serializable) additionalInformation.get(key));
                    }

                    return request;
                }
            };
        }

        @Bean
        public JwtAccessTokenConverter jwtAccessTokenConverter() {
            JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
            KeyPair keyPair = new KeyStoreKeyFactory(
                    new ClassPathResource("keystore.jks"), "newland".toCharArray())
                    .getKeyPair("keystore");
            converter.setKeyPair(keyPair);
            return converter;
        }

        @Bean
        public TokenEnhancer enhancer() {
            return (accessToken, authentication) -> {
                final Map<String, Object> additionalInfo = new HashMap<>();
                additionalInfo.put("grant_type", authentication.getOAuth2Request().getGrantType());
                Map<String, Serializable> extensions = authentication.getOAuth2Request().getExtensions();

                Object appId = extensions.get("app_id");
                Object account = extensions.get("account");
                if (appId != null) {additionalInfo.put("app_id", appId);}
                if (account != null) {additionalInfo.put("account", account);}

                ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
                return accessToken;
            };
        }

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            clients.withClientDetails(jdbcClientDetailsService(dataSource));
        }

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {

            // 将增强的token设置到增强链中
            TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
            enhancerChain.setTokenEnhancers(Arrays.asList(enhancer(), jwtAccessTokenConverter()));

            endpoints
                    .tokenEnhancer(enhancerChain)
                    .accessTokenConverter(jwtAccessTokenConverter())
                    .requestFactory(oAuth2RequestFactory())
                    .authenticationManager(authenticationManager);
        }

        @Override
        public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
            oauthServer
                    .allowFormAuthenticationForClients()
                    // url:/oauth/token_key,exposes public key for token verification if using JWT tokens 提供公有密匙的端点，如果使用JWT令牌的话
                    .tokenKeyAccess("permitAll()")
                    // url:/oauth/check_token allow check token 用于资源服务访问的令牌解析端点
                    .checkTokenAccess("isAuthenticated()");
        }
    }
}
