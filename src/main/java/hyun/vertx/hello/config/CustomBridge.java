package hyun.vertx.hello.config;

import hyun.vertx.hello.controller.ControllerInterface;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static hyun.vertx.hello.common.Common.formatting;

@Slf4j
public class CustomBridge {

  private final List<? extends ControllerInterface> controllers;
  private final ResponseComponent responseComponent = new ResponseComponent();
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
      processControllerMethods(router, controller, controllerClass);
    }
    long endTime = System.nanoTime();
    long elapsedTime = endTime - startTime;
    log.info("Handler mapping and request mapping completed in {} nanoseconds", formatting(elapsedTime));
  }

  private void processControllerMethods(Router router, ControllerInterface controller, Class<? extends ControllerInterface> controllerClass) {
    for (Method declaredMethod : controllerClass.getDeclaredMethods()) {
      Annotation[] annotations = declaredMethod.getAnnotations();
      processAnnotations(router, controller, declaredMethod, annotations);
    }
  }

  private void processAnnotations(Router router, ControllerInterface controller, Method declaredMethod, Annotation[] annotations) {
    for (Annotation annotation : annotations) {
      Method[] annotationMethods = annotation.annotationType().getMethods();
      processAnnotationMethods(router, controller, declaredMethod, annotationMethods, annotation);
    }
  }

  private void processAnnotationMethods(Router router, ControllerInterface controller, Method declaredMethod, Method[] annotationMethods, Annotation annotation) {
    for (Method annotationMethod : annotationMethods) {
      processAnnotationMethod(router, controller, declaredMethod, annotation, annotationMethod);
    }
  }

  private void processAnnotationMethod(Router router, ControllerInterface controller, Method declaredMethod, Annotation annotation, Method annotationMethod) {
    try {
      if (annotationMethod.getParameterCount() == 0 && annotationMethod.getDeclaringClass().equals(annotation.annotationType())) {
        Object value = annotationMethod.invoke(annotation);
        initializeRouter(annotation.annotationType().getSimpleName(), (String) value, router, controller, declaredMethod);
      }
    } catch (IllegalAccessException | InvocationTargetException e) {
      log.error("Server initial fail", e);
    }
  }

  private void initializeRouter(String annotation, String url, Router router, ControllerInterface controller, Method declaredMethod) {
    router.route(url).method(httpMethodMap.get(annotation)).handler(context -> {
      try {
        Object result = declaredMethod.invoke(controller);
        responseComponent.setResponse(context, result);
      } catch (IllegalAccessException | InvocationTargetException e) {
        log.error("server initial fail", e);
      }
    });
  }

}
