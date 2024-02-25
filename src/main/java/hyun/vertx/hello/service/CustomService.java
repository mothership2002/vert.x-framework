package hyun.vertx.hello.service;

import hyun.vertx.hello.domain.dto.Hello;
import hyun.vertx.hello.repository.CustomRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomService {

  private final CustomRepository customRepository;

  public Hello hello() {
    return new Hello("hello word");
  }

  public String connectRepo() {
    return customRepository.returnResult();
  }
}
