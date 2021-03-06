
package org.apache.ibatis.scripting;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;


public class LanguageDriverRegistry {

  private static final Log log = LogFactory.getLog(LanguageDriverRegistry.class);

  private final Map<Class<? extends LanguageDriver>, LanguageDriver> LANGUAGE_DRIVER_MAP = new HashMap<>();

  private Class<? extends LanguageDriver> defaultDriverClass;

  public LanguageDriverRegistry() { // -modify
    log.warn("进入 【LanguageDriverRegistry】 无参构造函数 {}");
  }

  public void register(Class<? extends LanguageDriver> cls) {
    if (cls == null) throw new IllegalArgumentException("null is not a valid Language Driver");
    LANGUAGE_DRIVER_MAP.computeIfAbsent(cls, k -> {
      try {
        return k.getDeclaredConstructor().newInstance();
      } catch (Exception ex) {
        throw new ScriptingException("Failed to load language driver for " + cls.getName(), ex);
      }
    });
  }

  public void register(LanguageDriver instance) {
    if (instance == null) throw new IllegalArgumentException("null is not a valid Language Driver");
    Class<? extends LanguageDriver> cls = instance.getClass();
    if (!LANGUAGE_DRIVER_MAP.containsKey(cls)) {
      LANGUAGE_DRIVER_MAP.put(cls, instance);
    }
  }

  public LanguageDriver getDefaultDriver() {
    return getDriver(getDefaultDriverClass());
  }

  public LanguageDriver getDriver(Class<? extends LanguageDriver> cls) {
    return LANGUAGE_DRIVER_MAP.get(cls);
  }

  public Class<? extends LanguageDriver> getDefaultDriverClass() {
    return defaultDriverClass;
  }

  public void setDefaultDriverClass(Class<? extends LanguageDriver> defaultDriverClass) {
    register(defaultDriverClass);
    this.defaultDriverClass = defaultDriverClass;
  }
}
