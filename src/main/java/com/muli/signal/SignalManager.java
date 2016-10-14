package com.muli.signal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * Created by IntelliJ IDEA.
 * User: wangweiwei
 * Date: 1/10/12
 * Time: 10:53 AM
 * To change this template use File | Settings | File Templates.
 */
@SuppressWarnings("restriction")
public class SignalManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(SignalManager.class);

    public static void install() {
        Signal.handle(new Signal("TERM"), new QuitSignalHandler());
        Signal.handle(new Signal("INT"), new QuitSignalHandler());
        LOGGER.info("signal handlers installed");
    }

    static class QuitSignalHandler implements SignalHandler {
        @Override
        public void handle(Signal signal) {
            LOGGER.info("signal:{} received", signal.getName());
            System.exit(0);
        }
    }
}
