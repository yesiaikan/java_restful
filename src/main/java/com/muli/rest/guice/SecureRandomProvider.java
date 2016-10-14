package com.muli.rest.guice;

import com.google.inject.Provider;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Created by IntelliJ IDEA.
 * User: wangweiwei
 * Date: 2/23/12
 * Time: 5:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class SecureRandomProvider implements Provider<SecureRandom> {
    @Override
    public SecureRandom get() {
        try {
            return SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return  null;
    }
}
