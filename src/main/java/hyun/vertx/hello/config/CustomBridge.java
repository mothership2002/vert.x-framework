package hyun.vertx.hello.config;

import hyun.vertx.hello.controller.ControllerInterface;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class CustomBridge {

  private final List<? extends ControllerInterface> controllers;
  private final Map<String, HttpMethod> httpMethodMap = new HashMap<>();

  public CustomBridge(List<? extends ControllerInterface> controllers) {
    this.controllers = controllers;
    httpMethodMap.put("GetMapping", HttpMethod.GET);
    httpMethodMap.put("PostMapping", HttpMethod.POST);
    httpMethodMap.put("PutMapping", HttpMethod.PUT);
    httpMethodMap.put("DeleteMapping", HttpMethod.DELETE);
    httpMethodMap.put("PatchMapping", HttpMethod.PATCH);
  }

  public void handlerMappingRequestMapping(Router router) {
    long startTime = System.nanoTime();
    for (ControllerInterface controller : controllers) {
      Class<? extends ControllerInterface> controllerClass = controller.getClass();
      for (Method declaredMethod : controllerClass.getDeclaredMethods()) {
        Annotation[] annotations = declaredMethod.getAnnotations();
        for (Annotation annotation : annotations) {
          Method[] annotationMethods = annotation.annotationType().getMethods();
          for (Method annotationMethod : annotationMethods) {
            // 이부분 코드해석이 필요
            try {
              if (annotationMethod.getParameterCount() == 0 && annotationMethod.getDeclaringClass().equals(annotation.annotationType())) {
                Object value = annotationMethod.invoke(annotation);
                initializeRouter(annotation.annotationType().getSimpleName(), (String) value, router, controllerClass, declaredMethod);
              }
            } catch (IllegalAccessException | InvocationTargetException e) {
              log.error("server initial fail");
            }
          }
        }
      }
    }
  }

  private void initializeRouter(String annotation, String url, Router router, Class<? extends ControllerInterface> controllerClass, Method declaredMethod) {
    router.route(url).method(httpMethodMap.get(annotation)).handler(context -> {
      try {
            Object controllerInstance = controllerClass.getDeclaredConstructor().newInstance();
            Object result = declaredMethod.invoke(controllerInstance);
            setResponse(context, result);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.error("server initial fail");
        }
    });
  }

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
    }
    else {
      json.put("result", dto);
    }
    return json;
  }

  private void setResponse(RoutingContext context, Object dto) {
    setHeader(context.response());
    if (!Objects.isNull(dto)) {
      try {
        context.json(parseResponseBody(dto));
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    }
  }


}