package hyun.vertx.hello.component.config;

import hyun.vertx.hello.CanNotSearchComponentConstructor;
import hyun.vertx.hello.controller.ControllerInterface;
import hyun.vertx.hello.service.Service;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class AppInitializeComponent {

  @Getter
  private final List<? extends ControllerInterface> controllerList;
  private final Map<Class<?>, Object> serviceMap;
  private final String ROOT = "hyun.vertx.hello";

  public AppInitializeComponent() {
    this.serviceMap = initService();
    this.controllerList = initController();
  }


  private List<? extends ControllerInterface> initController() {
    Reflections reflections = new Reflections(ROOT);
    Set<Class<? extends ControllerInterface>> classes = reflections.getSubTypesOf(ControllerInterface.class);
    // singleton -> List.copyOf()
    return List.copyOf(classes.stream().map(clazz -> {
      try {
        Constructor<?>[] declaredConstructors = clazz.getDeclaredConstructors();
        // only first one constructor -> isn't extend ?
        for (Constructor<?> declaredConstructor : declaredConstructors) {
          // public constructor
          Class<?>[] parameterTypes = declaredConstructor.getParameterTypes();
          Object[] param = Arrays.stream(parameterTypes).map(serviceMap::get).toArray();
          return (ControllerInterface) declaredConstructor.newInstance(param);
        }
        throw new CanNotSearchComponentConstructor(clazz);
      } catch (InvocationTargetException e) {
        Throwable cause = e.getTargetException();
        log.error("Error while invoking method or constructor", cause);
        throw new RuntimeException(cause);
      } catch (Exception e) {
        log.error("", e);
        throw new RuntimeException(e);
      }
    }).toList());
  }

  // service
  private Map<Class<?>, Object> initService() {
    Reflections reflections = new Reflections(ROOT);
    Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Service.class);
    return Map.copyOf(classes.stream().map(clazz -> {
      try {
        return clazz.getConstructor().newInstance();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }).collect(Collectors.toMap(Object::getClass, Function.identity())));
  }

  // repository


}
