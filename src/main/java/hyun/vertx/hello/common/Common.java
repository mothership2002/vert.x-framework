package hyun.vertx.hello.common;

import java.text.NumberFormat;
import java.util.Locale;

public class Common {

  public static String formatting(long time) {
    return NumberFormat.getNumberInstance(Locale.US).format(time);
  }

}
