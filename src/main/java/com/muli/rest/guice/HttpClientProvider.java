package com.muli.rest.guice;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;

/**
 * Created by IntelliJ IDEA.
 * User: wangweiwei
 * Date: 2/23/12
 * Time: 5:52 PM
 * To change this template use File | Settings | File Templates.
 */

public class HttpClientProvider implements Provider<HttpClient> {
    @Inject
    public HttpClientProvider() {
    }

    @Override
    public HttpClient get() {
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
        schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));

        PoolingClientConnectionManager cm = new PoolingClientConnectionManager(schemeRegistry);
        // Increase max total connection to 200
        cm.setMaxTotal(200);
        // Increase default max connection per route to 20
        cm.setDefaultMaxPerRoute(20);
        // Increase max connections for localhost:80 to 50
        HttpHost localhost = new HttpHost("locahost", 80);
        cm.setMaxPerRoute(new HttpRoute(localhost), 50);

        DefaultHttpClient httpClient = new DefaultHttpClient(cm);
        //

        HttpConnectionParams.setSoTimeout(httpClient.getParams(), 20000);
        HttpConnectionParams.setConnectionTimeout(httpClient.getParams(),
                5000);
        //		HttpClientParams.setRedirecting(httpClient.getParams(), false);
        httpClient.getParams().setIntParameter(ClientPNames.MAX_REDIRECTS, 10);

        HttpClientParams.setCookiePolicy(httpClient.getParams(), CookiePolicy.BROWSER_COMPATIBILITY);

        httpClient.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(3, true));


        HttpProtocolParams
                .setUserAgent(
                        httpClient.getParams(),
                        "Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.2.18) Gecko/20110628 Ubuntu/10.04 (lucid) Firefox/3.6.18");
        return httpClient;
    }
}
