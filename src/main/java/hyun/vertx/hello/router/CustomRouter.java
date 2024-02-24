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

    router.get("/").handler(context ->
      setResponse(context, null)
    );

    router.route("/hello").handler(context ->
      setResponse(context, null)
    );

    return router;
  }

  private void setHeader(HttpServerResponse resp) {
    resp.putHeader("content-type", "application/json");
  }

  private JsonObject parseResponseBody(Object dto) throws IllegalAccessException {
    JsonObject json = new JsonObject();
    for (Field declaredField : dto.getClass().getDeclaredFields()) {
      declaredField.setAccessible(true);
      json.put(declaredField.getName(), declaredField.get(dto));
    }
    return json;
  }

  private void setResponse(RoutingContext context, Object dto) {
    setHeader(context.response());
    if (!Objects.isNull(dto)) {
        try {
            context.json(parseResponseBody(dto));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
//    return context;
  }
}
