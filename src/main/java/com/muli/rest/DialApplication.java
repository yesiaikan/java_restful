package com.muli.rest;

import com.dianxinos.msp.auth.DXAuthenticationHelper;
import com.dianxinos.msp.auth.IAuthenticatorFactory;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.muli.guice.GuiceModule;
import com.muli.rest.api.impl.HelloWorld;
import com.muli.rest.guice.FinderFactory;
import com.muli.rest.guice.RestletGuice;
import com.muli.signal.SignalManager;
import com.muli.utils.CommonUtils;
import org.apache.commons.configuration.Configuration;
import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.engine.Engine;
import org.restlet.routing.Router;
import org.restlet.security.Authenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.<br/>
 * User: wangweiwei<br/>
 * Date: 11/1/11<br/>
 * Time: 9:04 PM<br/>
 * To change this template use File | Settings | File Templates.<br/>
 */
public class DialApplication extends Application {
    private static Logger LOGGER = LoggerFactory.getLogger(DialApplication.class);
    private Injector injector = null;

    public DialApplication(Injector injector) {
        this.injector = injector;

    }

    public DialApplication(Context context) {
        //setStatusService(new MyDefaultResource());
        super(context);
    }

    @Override
    public void start() throws Exception {
        super.start();


    }

    @Override
    public void stop() throws Exception {
        LOGGER.info("stopping...");
        super.stop();
        LOGGER.info("stopped");
    }

    @Override
    public Restlet createInboundRoot() {
        FinderFactory ff = injector.getInstance(FinderFactory.class);
        Router router = new Router(getContext());
        // 管理端访问接口；
//        router.attach("/_dianhua_api/health", ff.finder(HealthServerResource.class));
        router.attach("/java_restful/hello", ff.finder(HelloWorld.class));


        // 需要统一认证的接口列表，如下：
        Router guardRouter = new Router(getContext());
//        guardRouter.attach("taobao/query/remove", ff.finder(QueryTaobaoRemoveServerResource.class));

        IAuthenticatorFactory iAuthenticatorFactory = injector.getInstance(IAuthenticatorFactory.class);
        Authenticator authenticator = iAuthenticatorFactory.createAuthenticator(getContext());
        authenticator.setNext(guardRouter);

        router.attach("/java_restful/", authenticator); // 添加认证支持；
        return router;
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("org.restlet.engine.loggerFacadeClass", "org.restlet.ext.slf4j.Slf4jLoggerFacade");
        CommonUtils.loadLogbackConfiguration(CommonUtils.__CONF_DIR__);
        LOGGER.info("TotalMemory:" + Runtime.getRuntime().totalMemory() / (1024 * 1024) + " M");
        Engine.getInstance().getRegisteredAuthenticators().add(new DXAuthenticationHelper());
        final org.restlet.Component component = new org.restlet.Component();
        List<Module> modules = new ArrayList<Module>();
        modules.add(new GuiceModule());
        Injector injector = RestletGuice.createInjector(modules);
        Configuration configuration = injector.getInstance(Configuration.class);

        Application dialApp = new DialApplication(injector);
        component.getDefaultHost().attach(dialApp);
        component.getClients().add(Protocol.FILE);
        Server server = component.getServers().add(Protocol.HTTP, configuration.getInt("server.port"));
        // jetty config
        server.getContext().getParameters().add("minThreads", "50");
        server.getContext().getParameters().add("maxThreads", "1024");
        server.getContext().getParameters().add("acceptorThreads", "4");
        server.getContext().getParameters().add("gracefulShutdown", "5000");
        server.getContext().getParameters().add("useForwardedForHeader", "true");
        component.start();
        LOGGER.info("Sever started on " + Inet4Address.getLocalHost().getHostAddress() + ":" + configuration.getInt("server.port"));


        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    component.stop();
                    LOGGER.info("gracefully shutdown java_restful system");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        SignalManager.install();
    }
}
