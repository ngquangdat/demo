package com.example.demo.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.model.entity.Account;
import com.example.demo.service.JwtService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.secret-key}")
    private String secretKey;
    @Value("${jwt.private-key}")
    private String privateKey;
    @Value("${jwt.public-key}")
    private String publicKey;
    @Value("${jwt.expired-time}")
    private int expiredTime;
    @SneakyThrows
    public RSAPrivateKey readPrivateKey() {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream privateKeyStream = classloader.getResourceAsStream(privateKey);
        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader(privateKeyStream, StandardCharsets.UTF_8))) {
            int c;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
        }
        String key = textBuilder.toString();
        String privateKeyPEM = key
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PRIVATE KEY-----", "");

        byte[] encoded = Base64.decodeBase64(privateKeyPEM);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }

    @SneakyThrows
    public RSAPublicKey readPublicKey() {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream publicKeyStream = classloader.getResourceAsStream(publicKey);
        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader(publicKeyStream, StandardCharsets.UTF_8))) {
            int c;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
        }
        String key = textBuilder.toString();
        String publicKeyPEM = key
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PUBLIC KEY-----", "");

        byte[] encoded = Base64.decodeBase64(publicKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    public String generateTokenLogin(Account account) {
        // Asymmetric encryption
//        Algorithm algorithm = Algorithm.RSA256(readPublicKey(), readPrivateKey());
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, expiredTime);
        return JWT.create()
                .withIssuer("datnq")
                .withClaim("username", account.getUsername())
                .withClaim("phone", account.getPhone())
                .withClaim("point", account.getPoint())
                .withExpiresAt(calendar.getTime())
                .sign(algorithm);
    }

    public String validateJwtToken(String token) {
        try {
            // Asymmetric encryption
//            Algorithm algorithm = Algorithm.RSA256(readPublicKey(), null);
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("datnq")
                    .build();

            verifier.verify(token);
        } catch (JWTVerificationException exception){
            log.warn("validateJwtToken fail", exception);
            return exception.getMessage();
        }
        return null;
    }

    @Override
    public Map<String, Object> getPayload(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("datnq")
                .build();

        DecodedJWT decodedJWT = verifier.verify(token);
        Map<String, Claim> map = decodedJWT.getClaims();
        Map<String, Object> resultMap = new HashMap<>();
        map.forEach((k,v) -> resultMap.put(k, v.asString() == null ? v.asLong() : v.asString()));
        return resultMap;
    }

}
