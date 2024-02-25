package hyun.vertx.hello.service;

import hyun.vertx.hello.domain.dto.Hello;

@Service
public class CustomService {

  public Hello hello() {
    return new Hello("hello word");
  }
}
