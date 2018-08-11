package com.umic.vertx;

import com.umic.config.BaseLogger;
import com.umic.config.ConfigLoader;
import com.umic.hotlink.MTLHotLink;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.FaviconHandler;
import io.vertx.ext.web.handler.StaticHandler;
import com.umic.utils.RSSParser;
import netscape.javascript.JSObject;

import java.util.List;

/**
 * Created by umic_ on 2018/3/24.
 */
public class BaseWebServer extends BaseLogger {
    private static BaseWebServer baseWebServer = null;
    private static long count = 0;
    private static String PROJECT_DIR = "./";

    static {
        PROJECT_DIR = System.getProperty("user.dir");
        ConfigLoader.loadConfig(PROJECT_DIR + "/config/config.dat");

    }

    private final HttpServer server;
    private String host = ConfigLoader.getValue(ConfigLoader.HOST);
    private String port = ConfigLoader.getValue(ConfigLoader.PORT);
    private String domain = ConfigLoader.getValue(ConfigLoader.DOMAIN);

    private BaseWebServer(HttpServer server) {
        getLogger().info(PROJECT_DIR);
        this.server = server;
        this.getLogger().info("com.umic.vertx.BaseWebServer Construction");
    }

    public static BaseWebServer newInstance() {
        return (baseWebServer = baseWebServer == null ? new BaseWebServer(VertxCore.newInstance().getServer()) : baseWebServer);
    }

    private void addRouter() {
        Router router = Router.router(VertxCore.newInstance().getVertx());
        router.routeWithRegex(".*").handler(rc -> {
            HttpServerRequest request = rc.request();
            getLogger().info(request.headers().get("host").trim());
            if (domain.equals(request.headers().get("host").trim())) {
                rc.next();
            } else {
                rc.fail(404);
            }
        });
        router.route("/static/*").handler(StaticHandler.create());
        router.route("/favicon.ico").handler(FaviconHandler.create(PROJECT_DIR + "/webroot/favicon.ico"));
        router.get("/").handler(rc -> {
            rc.reroute("/index");
        });
        router.get("/index.*").handler(rc -> {
            rc.reroute("/index");
        });
        router.get("/index").handler(rc -> {
            rc.response().putHeader("Content-Type", "text/html;charset=utf-8");
            getLogger().info(PROJECT_DIR + "/webroot/index.html");
            rc.response().sendFile(PROJECT_DIR + "/webroot/index.html");
        });
        router.get("/robots.txt").handler(rc -> {
            rc.response().sendFile(PROJECT_DIR + "/webroot/robots.txt");
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
                            String[] kv = subItem.split("#", 2);
                            if (kv.length > 1) {
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
        router.get("/feed/resource/picture/:id/:page").handler(rc -> {
            VertxCore.newInstance().getVertx().executeBlocking(future -> {
                String page = rc.request().getParam("page");
                String id = rc.request().getParam("id");
                JsonObject jsonObject = new JsonObject();
                jsonObject.put("page", page);
                jsonObject.put("id", id);
                jsonObject.put("data", MTLHotLink.getInstance().getPicture(id, page));
                future.complete(jsonObject.toString());
            }, res -> {
                try {
                    if (!rc.response().closed()) {
                        rc.response().end((String) res.result());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (!rc.response().closed()) {
                        rc.response().end();
                    }
                }
            });

        });
        router.get("/feed/list").handler(rc -> {
            rc.response().sendFile(PROJECT_DIR + "/webroot/pictures.html");
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
