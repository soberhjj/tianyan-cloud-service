package com.newland.tianyan.gateway.encrypt;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

public class AESUtils {

    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";

    public static SecretKey getSecretKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            // for example
            keyGen.init(256);
            return keyGen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static String secretToString(SecretKey secretKey) {
        // get base64 encoded version of the key
        return Base64.encodeBase64String(secretKey.getEncoded());
    }

    public static SecretKey stringToSecret(String key) {
        // decode the base64 encoded string
        byte[] decodedKey = Base64.decodeBase64(key);
        // rebuild key using SecretKeySpec
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    /**
     * 加密
     *
     * @param content   要加密的内容
     * @param secretKey 密钥
     */
    public static String encrypt(String content, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] bytes = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeBase64String(bytes);
    }

    /**
     * 解密
     *
     * @param encryptStr 解密的字符串(Base64编码)
     * @param secretKey  解密的key值
     */
    public static String decrypt(String encryptStr, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] encryptBytes = Base64.decodeBase64(encryptStr);
        byte[] decryptBytes = cipher.doFinal(encryptBytes);
        return new String(decryptBytes);
    }
}
