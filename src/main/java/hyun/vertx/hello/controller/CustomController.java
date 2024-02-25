package hyun.vertx.hello.controller;

import hyun.vertx.hello.controller.annotation.GetMapping;
import hyun.vertx.hello.controller.annotation.PostMapping;
import hyun.vertx.hello.controller.annotation.PutMapping;
import hyun.vertx.hello.domain.dto.Hello;
import hyun.vertx.hello.service.CustomService;
import lombok.NoArgsConstructor;

public class CustomController implements ControllerInterface {

  private final CustomService customService;

  public CustomController(CustomService customService) {
    this.customService = customService;
  }

  @GetMapping("/")
  public String home() {
    return "home";
  }

  @PostMapping("/hello")
  public String hello() {
    return "hello world";
  }

  @PutMapping("/put")
  public Hello put() {
    return customService.hello();
  }
}
