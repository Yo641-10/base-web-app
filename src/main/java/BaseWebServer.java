import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.SocketAddress;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.ErrorHandler;
import io.vertx.ext.web.handler.StaticHandler;

/**
 * Created by umic_ on 2018/3/24.
 */
public class BaseWebServer extends BaseLogger {
    private static BaseWebServer baseWebServer = null;
    private static long count = 0;


    public static BaseWebServer newInstance() {
        return (baseWebServer = baseWebServer == null ? new BaseWebServer(VertxCore.newInstance().getServer()) : baseWebServer);
    }

    private final HttpServer server;


    private BaseWebServer(HttpServer server) {
        this.server = server;
        this.getLogger().info("BaseWebServer Construction");
    }

    private void addRouter() {
        Router router = Router.router(VertxCore.newInstance().getVertx());

        router.routeWithRegex(".*").handler(rc -> {
            HttpServerRequest request = rc.request();
            BaseWebServer.newInstance().getLogger().info(request.localAddress().toString());
            BaseWebServer.newInstance().getLogger().info(request.absoluteURI());
            if (GlobalConstants.LOCAL_ADDRESS.equals(rc.request().headers().get("host").trim())) {
                rc.next();
            } else {
                rc.fail(404);
            }
        });
        router.route("/static/*").handler(StaticHandler.create());
        router.get("/").handler(rc -> {
            rc.reroute("/index");
        });
        router.get("/index").handler(rc -> {
            rc.response().putHeader("Content-Type", "text/html;charset=utf-8");
            rc.response().sendFile("index.html");
        });
        router.get("/robots.txt").handler(rc -> {
            rc.response().end("User-agent: *\nDisallow: /");
        });
        router.get("/action/list-novels").handler(rc -> {
            JsonObject novels_data = new JsonObject();
            JsonArray novels = new JsonArray();
            for (int i = 0; i < 10; ++i) {
                JsonObject novel = new JsonObject();
                novel.put("title", "东航旅客机上突然昏迷 航班空中放油30吨备降救人");
                novel.put("subtitle", "北京时间3月23日19:37，东航从上海飞往纽约的MU587航班为了救治一名60岁的女性旅客，空中放油30吨，紧急备降安克雷奇机场。");
                novel.put("author", "中国法院网");
                novel.put("date", "2018-03-24 20:37:13 ");
                novel.put("url", "http://news.china.com/socialgd/10000169/20180324/32225122.html");
                novels.add(novel);
            }
            novels_data.put("data", novels);
            novels_data.put("status", "400");
            rc.response().end(novels_data.toString());
        });
        this.server.requestHandler(router::accept);
    }

    private void init() {
        this.addRouter();
    }

    public void start() {
        this.init();
        this.server.listen(8000, "0.0.0.0");
    }

    public static void main(String[] args) throws Exception {
        BaseWebServer.newInstance().start();
    }
}
