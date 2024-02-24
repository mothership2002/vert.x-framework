package hyun.vertx.hello;

import hyun.vertx.hello.domain.dto.Hello;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

public class SomethingTest {

  @Test
  void test() {
    Hello hello = new Hello();
    hello.setWorld("hello world");
    testMethod(hello, hello.getClass());
      try {
          createResponseBody(hello);
      } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
      }
  }

  void testMethod(Object dto, Class<?> clazz) {
    System.out.println(dto.getClass());
  }


  private JsonObject createResponseBody(Object dto) throws IllegalAccessException {
    JsonObject json = new JsonObject();

    for (Field declaredField : dto.getClass().getDeclaredFields()) {
      declaredField.setAccessible(true);
      System.out.println(declaredField.getName());
      System.out.println(declaredField.get(dto));
      json.put(declaredField.getName(), declaredField.get(dto));
    }
    return json;
  }
}
