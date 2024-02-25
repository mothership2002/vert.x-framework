package hyun.vertx.hello;

import hyun.vertx.hello.common.Common;
import hyun.vertx.hello.component.config.AppInitializeComponent;
import hyun.vertx.hello.component.config.CustomBridge;
import hyun.vertx.hello.router.CustomRouter;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) {
    long startTime = System.nanoTime();
    CustomRouter router = null;
    try {
      AppInitializeComponent appInitializeComponent = new AppInitializeComponent();
      CustomBridge bridge = new CustomBridge(appInitializeComponent.getControllerList());
      router = new CustomRouter(bridge);
    } catch (Exception e) {
      startPromise.fail(e);
      System.exit(0);
    }

    vertx.createHttpServer()
      .requestHandler(router.getRouter(vertx))
      .listen(8080)
      .onSuccess(server -> {
        log.info("server initialized time : {} ns", Common.formatting(System.nanoTime() - startTime));
        log.info("server start " + server.actualPort());
      });
  }
}
