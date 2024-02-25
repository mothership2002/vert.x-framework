package hyun.vertx.hello.component.config;

import hyun.vertx.hello.exception.CanNotSearchComponentConstructor;
import hyun.vertx.hello.controller.ControllerInterface;
import hyun.vertx.hello.repository.RepositoryAndApiConnectorInterface;
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
  private final Map<Class<?>, RepositoryAndApiConnectorInterface> daoMap;
  private final String ROOT = "hyun.vertx.hello";
  private final Reflections reflections = new Reflections(ROOT);

  public AppInitializeComponent() {
    this.daoMap = initDao();
    this.serviceMap = initService();
    this.controllerList = initController();
  }


  private List<? extends ControllerInterface> initController() {
    Set<Class<? extends ControllerInterface>> classes = reflections.getSubTypesOf(ControllerInterface.class);
    // singleton -> List.copyOf()
    return List.copyOf(classes.stream().map(clazz -> {
      try {
        Object declaredConstructor = getComponent(clazz);
        if (declaredConstructor != null) {
          return (ControllerInterface) declaredConstructor;
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
    Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Service.class);
    return Map.copyOf(classes.stream().map(clazz -> {
      try {
        Object declaredConstructor = getComponent(clazz);
        if (declaredConstructor != null) {
          return declaredConstructor;
        }
        throw new CanNotSearchComponentConstructor(clazz);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }).collect(Collectors.toMap(Object::getClass, Function.identity())));
  }

  // repository
  // i think that need to jdbc
  private Map<Class<?>, RepositoryAndApiConnectorInterface> initDao() {
    Set<Class<? extends RepositoryAndApiConnectorInterface>> subTypesOf = reflections.getSubTypesOf(RepositoryAndApiConnectorInterface.class);
    return Map.copyOf(subTypesOf.stream().map(dao -> {
        try {
          return (RepositoryAndApiConnectorInterface) dao.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }).collect(Collectors.toMap(Object::getClass, Function.identity())));
  }

  private Object getComponent(Class<?> clazz) throws InstantiationException, IllegalAccessException, InvocationTargetException {
    Constructor<?>[] declaredConstructors = clazz.getDeclaredConstructors();
    // only first one constructor -> isn't extend ?
    for (Constructor<?> declaredConstructor : declaredConstructors) {
      // public constructor
      Class<?>[] parameterTypes = declaredConstructor.getParameterTypes();
      if (ControllerInterface.class.isAssignableFrom(clazz)) {
        Object[] param = Arrays.stream(parameterTypes).map(serviceMap::get).toArray();
        return declaredConstructor.newInstance(param);
      }
      else {
        Object[] param = Arrays.stream(parameterTypes).map(daoMap::get).toArray();
        return declaredConstructor.newInstance(param);
      }
    }
    return null;
  }

}
