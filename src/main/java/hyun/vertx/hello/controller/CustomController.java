package hyun.vertx.hello.controller;

import hyun.vertx.hello.controller.annotation.GetMapping;
import hyun.vertx.hello.controller.annotation.PostMapping;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CustomController implements ControllerInterface {

  @GetMapping("/")
  public String home() {
    return "home";
  }

  @PostMapping("/hello")
  public String hello() {
    return "hello world";
  }
}
