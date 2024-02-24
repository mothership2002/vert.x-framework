package hyun.vertx.hello.config;

import hyun.vertx.hello.controller.ControllerInterface;
import io.vertx.ext.web.Router;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class CustomBridge {

  private final List<? extends ControllerInterface> controllers;

  public CustomBridge(List<? extends ControllerInterface> controllers) {
    this.controllers = controllers;
  }

  //  public void handlerMappingRequestMapping(Router router) throws NoSuchFieldException, InvocationTargetException, IllegalAccessException {
//    for (ControllerInterface controller : controllers) {
//      Class<? extends ControllerInterface> controllerClass = controller.getClass();
//      for (Method declaredMethod : controllerClass.getDeclaredMethods()) {
//        for (Annotation declaredAnnotation : declaredMethod.getDeclaredAnnotations()) {
//          String name = declaredAnnotation.getClass().getName();
//          Method[] annotationMethods = declaredAnnotation.annotationType().getMethods();
//          for (Method annotationMethod : annotationMethods) {
//
//          }
//          System.out.println(name);
//
//        }
//      }
//    }
//  }
  public void handlerMappingRequestMapping(Router router) {
    for (ControllerInterface controller : controllers) {
      Class<? extends ControllerInterface> controllerClass = controller.getClass();
      for (Method declaredMethod : controllerClass.getDeclaredMethods()) {
        Annotation[] annotations = declaredMethod.getAnnotations();
        for (Annotation annotation : annotations) {
          Method[] annotationMethods = annotation.annotationType().getMethods();
          for (Method annotationMethod : annotationMethods) {
            try {
              if (annotationMethod.getParameterCount() == 0 && annotationMethod.getDeclaringClass().equals(annotation.annotationType())) { // Only process methods from the annotation interface itself with no parameters
                Object value = annotationMethod.invoke(annotation);
                System.out.println("Annotation method: " + annotationMethod.getName());
                System.out.println("Value: " + value);
              }
            } catch (IllegalAccessException | InvocationTargetException e) {
              e.printStackTrace();
            }
          }
        }
      }
    }
  }


}
