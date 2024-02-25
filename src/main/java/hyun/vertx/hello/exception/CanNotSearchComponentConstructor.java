package hyun.vertx.hello.exception;

import hyun.vertx.hello.controller.ControllerInterface;

public class CanNotSearchComponentConstructor extends RuntimeException {
  public CanNotSearchComponentConstructor(Class<?> clazz) {
    super("Couldn't Search Constructor : " + clazz);
  }
}
