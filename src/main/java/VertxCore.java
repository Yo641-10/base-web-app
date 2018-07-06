import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;

import static java.lang.System.out;

/**
 * Created by umic_ on 2018/3/24.
 */
public class VertxCore extends BaseLogger {
    private static VertxCore vertxCore = null;
    private final HttpServer server;
    private final Vertx vertx;

    public static VertxCore newInstance() {
        return (vertxCore = vertxCore == null ? new VertxCore() : vertxCore);
    }

    public Vertx getVertx() {
        return vertx;
    }

    private VertxCore() {
        VertxOptions vertxOptions = new VertxOptions();

        vertxOptions.setWorkerPoolSize(40);
        vertx = Vertx.vertx(vertxOptions);
        getLogger().info("Init Vertx 40 workers.");
        HttpServerOptions options = new HttpServerOptions().setMaxWebsocketFrameSize(1000000);
        server = vertx.createHttpServer(options);
    }

    public static VertxCore getVertxCore() {
        return vertxCore;
    }

    public HttpServer getServer() {
        return server;
    }

    public static void main(String[] args) throws Exception {
//        VertxCore.newInstance();
//        VertxCore.newInstance();
   }

}
