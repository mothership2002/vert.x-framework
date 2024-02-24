package hyun.vertx.hello.controller.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface PatchMapping {
  String value() default "";
}
