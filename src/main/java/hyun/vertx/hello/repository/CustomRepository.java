package hyun.vertx.hello.repository;

public class CustomRepository implements RepositoryAndApiConnectorInterface {

  public String returnResult() {
    return "result is success";
  }
}
