import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.FaviconHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.List;

import static java.lang.System.out;

/**
 * Created by umic_ on 2018/3/24.
 */
public class BaseWebServer extends BaseLogger {
    private static BaseWebServer baseWebServer = null;
    private static long count = 0;

    static {
        ConfigLoader.loadConfig("config/config.dat");

    }

    private final HttpServer server;
    private String host = ConfigLoader.getValue(ConfigLoader.HOST);
    private String port = ConfigLoader.getValue(ConfigLoader.PORT);

    private BaseWebServer(HttpServer server) {
        this.server = server;
        this.getLogger().info("BaseWebServer Construction");
    }

    public static BaseWebServer newInstance() {
        return (baseWebServer = baseWebServer == null ? new BaseWebServer(VertxCore.newInstance().getServer()) : baseWebServer);
    }

    public static void main(String[] args) throws Exception {
        BaseWebServer.newInstance().start();
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
        router.route("/favicon.ico").handler(FaviconHandler.create("webroot/favicon.ico"));
        router.get("/").handler(rc -> {
            rc.reroute("/index");
        });
        router.get("/index").handler(rc -> {
            rc.response().putHeader("Content-Type", "text/html;charset=utf-8");
            rc.response().sendFile("index.html");
        });
        router.get("/robots.txt").handler(rc -> {
            rc.response().sendFile("webroot/robots.txt");
        });
        router.get("/action/list-novels").handler(rc -> {
            VertxCore.newInstance().getVertx().executeBlocking(future -> {
                JsonObject novels_data = new JsonObject();
                try {
                    List<List<String>> itemList = RSSParser.parse("http://rss.sina.com.cn/news/china/focus15.xml");
                    JsonArray novels = new JsonArray();
                    itemList.forEach(item -> {
                        JsonObject novel = new JsonObject();
                        item.forEach(subItem -> {
                            String[] kv = subItem.split("#",2);
                            if (kv.length > 1) {
                                out.println(kv[0] + " " + kv[1]);
                                novel.put(kv[0], kv[1].trim());
                            }
                        });
                        novels.add(novel);
                    });
                    novels_data.put("data", novels);
                    novels_data.put("status", "400");
                } catch (Exception e) {
                    e.printStackTrace();
                    novels_data.put("status", "401");
                }
                future.complete(novels_data.toString());
            }, res -> {
                rc.response().end((String) res.result());
            });


        });
        this.server.requestHandler(router::accept);
    }

    private void init() {
        this.addRouter();
    }

    public void start() {
        this.init();
        this.server.listen(Integer.parseInt(this.port), this.host);
    }
}
