package com.newland.tianyan.auth.controller;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;

/**
 * @Author: huangJunJie  2021-03-04 14:27
 */
@RestController
public class KeyPairController {

    @GetMapping("/rsa/publicKey")
    public Map<String, Object> getKey() {
        PublicKey publicKey = new KeyStoreKeyFactory(
                new ClassPathResource("keystore.jks"), "newland".toCharArray())
                .getKeyPair("keystore").getPublic();
        RSAKey rsaKey = new RSAKey.Builder((RSAPublicKey) publicKey).build();
        return new JWKSet(rsaKey).toJSONObject();
    }
}
