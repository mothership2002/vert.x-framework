package hyun.vertx.hello;

import hyun.vertx.hello.config.CustomBridge;
import hyun.vertx.hello.controller.ControllerInterface;
import hyun.vertx.hello.router.CustomRouter;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.reflections.Reflections;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) {
    Reflections reflections = new Reflections("hyun.vertx.hello");
    Set<Class<? extends ControllerInterface>> classes = reflections.getSubTypesOf(ControllerInterface.class);
    List<? extends ControllerInterface> controllerList = classes.stream().map(clazz -> {
      try {
        return clazz.getConstructor().newInstance();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }).toList();

    CustomBridge bridge = new CustomBridge(controllerList);
    CustomRouter router = new CustomRouter(bridge);
    vertx.createHttpServer()
      .requestHandler(router.getRouter(vertx))
      .listen(8080)
      .onSuccess(server -> System.out.println("server start " + server.actualPort()));
  }
}
