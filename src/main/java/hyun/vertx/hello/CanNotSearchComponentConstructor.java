package hyun.vertx.hello;

import hyun.vertx.hello.controller.ControllerInterface;

public class CanNotSearchComponentConstructor extends RuntimeException {
  public CanNotSearchComponentConstructor(Class<? extends ControllerInterface> clazz) {
    super("Couldn't Search Constructor : " + clazz);
  }
}
