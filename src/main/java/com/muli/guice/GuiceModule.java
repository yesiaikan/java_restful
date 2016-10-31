package com.muli.guice;

import com.dianxinos.msp.auth.IAuthenticatorFactory;
import com.dianxinos.msp.auth.impl.AuthenticatorFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.muli.http.CloseableHttpClientProvider;
import com.muli.rest.guice.RSAPrivateKeyProvider;
import com.muli.rest.guice.RSAPublicKeyProvider;
import com.muli.rest.guice.SecureRandomProvider;
import com.muli.utils.CommonUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

/**
 * Created by muli1 on 16/10/14.
 */
public class GuiceModule extends AbstractModule {
    private static final Logger LOGGER = LoggerFactory.getLogger(GuiceModule.class);

    @Override
    protected void configure() {
        // provider
        bind(SecureRandom.class).toProvider(SecureRandomProvider.class).in(Singleton.class);
        bind(PublicKey.class).toProvider(RSAPublicKeyProvider.class).in(Singleton.class);
        bind(PrivateKey.class).toProvider(RSAPrivateKeyProvider.class).in(Singleton.class);
//         bind(HttpClient.class).toProvider(HttpClientProvider.class).in(Singleton.class);
        bind(HttpClient.class).toProvider(CloseableHttpClientProvider.class);
        bind(IAuthenticatorFactory.class).to(AuthenticatorFactory.class).in(Singleton.class);

//        bind(CacheService.class).to(CacheServiceImpl.class).in(Singleton.class);

    }

    @Provides
    @Singleton
    Configuration provideConfiguration() {
        return CommonUtils.getConfiguration(CommonUtils.__CONF_DIR__, "project.properties");
    }
}

