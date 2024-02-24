package hyun.vertx.hello.router;

import hyun.vertx.hello.config.CustomBridge;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
      log.error("route constructed fail", e);
    }
    return router;
  }
}
