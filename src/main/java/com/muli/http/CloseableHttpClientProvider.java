package com.muli.http;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.apache.commons.configuration.Configuration;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.params.SyncBasicHttpParams;

/**
 * Despt: 支持httpclient4.2及以上版本；
 * Author: zhangjunhong
 */
public class CloseableHttpClientProvider implements Provider<HttpClient> {
//    private static final Logger LOGGER = LoggerFactory.getLogger(CloseableHttpClientProvider.class);
   

    @Inject
    public CloseableHttpClientProvider(Configuration configuration) {
       
    }

    @Override
    public HttpClient get() {
        HttpParams params = new SyncBasicHttpParams();
        params.setParameter(HttpProtocolParams.PROTOCOL_VERSION,
                HttpVersion.HTTP_1_1);
        params.setBooleanParameter(HttpProtocolParams.USE_EXPECT_CONTINUE,
                true);
        params.setBooleanParameter(HttpConnectionParams.STALE_CONNECTION_CHECK,
                true);
        params.setIntParameter(HttpConnectionParams.SOCKET_BUFFER_SIZE,
                8 * 1024);
        params.setIntParameter(HttpConnectionParams.SO_TIMEOUT, 15000);
        params.setIntParameter(HttpConnectionParams.CONNECTION_TIMEOUT, 5000);
        params.setLongParameter(ClientPNames.CONN_MANAGER_TIMEOUT, 1000l); //该值就是连接不够用的时候等待超时时间，一定要设置，而且不能太大
        params.setBooleanParameter(HttpConnectionParams.TCP_NODELAY, true);
        params.setBooleanParameter(HttpConnectionParams.SO_REUSEADDR, true);
//        params.setBooleanParameter(HttpConnectionParams.SO_KEEPALIVE, true);

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
        schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));

        PoolingClientConnectionManager mgr = new PoolingClientConnectionManager(schemeRegistry);
        mgr.setMaxTotal(2000);
        mgr.setDefaultMaxPerRoute(500);


        DefaultHttpClient httpclient = new DefaultHttpClient(mgr, params);
        httpclient.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(0, false));

        HttpClientParams.setCookiePolicy(httpclient.getParams(), CookiePolicy.BROWSER_COMPATIBILITY);
        HttpProtocolParams.setUserAgent(httpclient.getParams(),
                "Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.2.18) Gecko/20110628 Ubuntu/10.04 (lucid) Firefox/3.6.18");
        return httpclient;
    }

//    @Override
//    public CloseableHttpClient get() {
//        try {
//            SSLContext sslContext = SSLContexts.createSystemDefault();
//            X509HostnameVerifier allowAllHostnameVerifier = new AllowAllHostnameVerifier();
//            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
//                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
//                    .register("https", new SSLConnectionSocketFactory(sslContext, allowAllHostnameVerifier))
//                    .build();
//
//            PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry,
//                    null, null, SystemDefaultDnsResolver.INSTANCE, 10000l, TimeUnit.MILLISECONDS);
//            ConnectionConfig connectionConfig = ConnectionConfig.custom()
//                    .setCharset(Consts.UTF_8)
//                    .setMalformedInputAction(CodingErrorAction.IGNORE)
//                    .setUnmappableInputAction(CodingErrorAction.IGNORE)
//                    .build();
//            RequestConfig requestConfig = RequestConfig.custom()
//                    .setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY)
//                    .setStaleConnectionCheckEnabled(false)
//                    .setConnectTimeout(10000)
//                    .setSocketTimeout(20000)
//                    .setMaxRedirects(5)
//                    .setConnectionRequestTimeout(this.configuration.getInt("http.request.timeout", 5000))
//                    .build();
//            SocketConfig socketConfig = SocketConfig.custom()
//                    .setTcpNoDelay(true)
//                    .setSoReuseAddress(true)
//                    .build();
//
//            connectionManager.setMaxTotal(2000);
//            connectionManager.setDefaultMaxPerRoute(50);
//            connectionManager.setDefaultConnectionConfig(connectionConfig);
//            connectionManager.setDefaultSocketConfig(socketConfig);
////            // Increase max connections for localhost:80 to 50
////            HttpHost localhost = new HttpHost("localhost", 80);
////            connectionManager.setMaxPerRoute(new HttpRoute(localhost), 400);
//            connectionManager.closeExpiredConnections();
//
//            HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
//            httpClientBuilder.setConnectionManager(connectionManager);
//            httpClientBuilder.setDefaultRequestConfig(requestConfig)
//                    .setConnectionReuseStrategy(new DefaultConnectionReuseStrategy())
//                    .setRetryHandler(new DefaultHttpRequestRetryHandler(0, false))
//                    .setUserAgent("Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.2.18) Gecko/20110628 Ubuntu/10.04 (lucid) Firefox/3.6.18")
//                    .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy());
//            return httpClientBuilder.build();
//        } catch (Exception e) {
//            LOGGER.error("Don't create httpclient, detail:{}", e);
//        }
//
//        return null;
//    }
}
