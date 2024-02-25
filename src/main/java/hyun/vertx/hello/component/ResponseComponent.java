package hyun.vertx.hello.component;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.lang.reflect.Field;
import java.util.Objects;

public class ResponseComponent {


  private void setHeader(HttpServerResponse resp) {
    resp.putHeader("content-type", "application/json");
  }

  private JsonObject parseResponseBody(Object dto) throws IllegalAccessException {
    JsonObject json = new JsonObject();
    if (!(dto instanceof String)) {
      for (Field declaredField : dto.getClass().getDeclaredFields()) {
        declaredField.setAccessible(true);
        json.put(declaredField.getName(), declaredField.get(dto));
      }
    } else {
      json.put("result", dto);
    }
    return json;
  }

  private JsonObject parseError(Throwable e) {
    return new JsonObject().put("exception", e.getClass().getSimpleName());
  }

  public void setResponse(RoutingContext context, Object dto) {
    setHeader(context.response());
    if (!Objects.isNull(dto)) {
      try {
        context.json(parseResponseBody(dto));
      } catch (IllegalAccessException e) {
        context.json(parseError(e));
        throw new RuntimeException(e);
      }
    }
  }

}
