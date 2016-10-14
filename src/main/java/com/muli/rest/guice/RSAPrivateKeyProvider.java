package com.muli.rest.guice;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.apache.commons.configuration.Configuration;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.RSAPrivateCrtKeySpec;

/**
 * Created by IntelliJ IDEA.
 * User: wangweiwei
 * Date: 2/23/12
 * Time: 5:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class RSAPrivateKeyProvider implements Provider<PrivateKey> {
    private Configuration configuration;

    @Inject
    public RSAPrivateKeyProvider(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public PrivateKey get() {
        BigInteger modulus = new BigInteger(configuration.getString("rsa.modulus"));
        BigInteger publicExponent = new BigInteger(configuration.getString("rsa.public.exponent"));
        BigInteger privateExponent = new BigInteger(configuration.getString("rsa.private.exponent"));
        BigInteger primeP = new BigInteger(configuration.getString("rsa.prime.p"));
        BigInteger primeQ = new BigInteger(configuration.getString("rsa.prime.q"));
        BigInteger primeExponentP = new BigInteger(configuration.getString("rsa.prime.exponent.p"));
        BigInteger primeExponentQ = new BigInteger(configuration.getString("rsa.prime.exponent.q"));
        BigInteger crtCoefficient = new BigInteger(configuration.getString("rsa.crt.coefficient"));
        RSAPrivateCrtKeySpec privateCrtKeySpec = new RSAPrivateCrtKeySpec(modulus, publicExponent, privateExponent, primeP, primeQ, primeExponentP, primeExponentQ, crtCoefficient);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(privateCrtKeySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
