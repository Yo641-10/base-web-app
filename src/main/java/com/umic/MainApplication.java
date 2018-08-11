package com.umic;

import com.umic.vertx.BaseWebServer;

/**
 * Created by umic_ on 2018/3/31.
 */
public class MainApplication {
    public static void main(String[] args) throws Exception {
        BaseWebServer.newInstance().start();
    }
}
