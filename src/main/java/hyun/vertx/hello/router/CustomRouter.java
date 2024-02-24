package hyun.vertx.hello.router;

import hyun.vertx.hello.config.CustomBridge;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.lang.reflect.Field;
import java.util.Objects;

public class CustomRouter {

  private final CustomBridge bridge;

  public CustomRouter(CustomBridge bridge) {
    this.bridge = bridge;
  }

  public Router getRouter(Vertx vertx) {
    Router router = Router.router(vertx);
    try {
      bridge.handlerMappingRequestMapping(router);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return router;
  }
}
