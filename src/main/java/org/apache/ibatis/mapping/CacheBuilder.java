
package org.apache.ibatis.mapping;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.builder.InitializingObject;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.cache.CacheException;
import org.apache.ibatis.cache.decorators.BlockingCache;
import org.apache.ibatis.cache.decorators.LoggingCache;
import org.apache.ibatis.cache.decorators.LruCache;
import org.apache.ibatis.cache.decorators.ScheduledCache;
import org.apache.ibatis.cache.decorators.SerializedCache;
import org.apache.ibatis.cache.decorators.SynchronizedCache;
import org.apache.ibatis.cache.impl.PerpetualCache;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

/**
 * 建造者模式（该类就是构造者）
 * 其中CacheBilder为建造者角色，Cache对象是产品角色，可以看CacheBuilder的源码来理解：
*/
public class CacheBuilder {

  // 这几个属性就是为生成产品对象需要的字段
  private final String id;
  private Class<? extends Cache> implementation;
  private final List<Class<? extends Cache>> decorators;
  private Integer size;
  private Long clearInterval;
  private boolean readWrite;
  private Properties properties;
  private boolean blocking;

  public CacheBuilder(String id) {
    this.id = id;
    this.decorators = new ArrayList<>();
  }

  // 以下这几个方法就是构造者在生成产品对象时，需要使用到的几个具体模块方法。可以根据这几个方法的不同组合，生成不同的Cache对象
  public CacheBuilder implementation(Class<? extends Cache> implementation) {
    this.implementation = implementation;
    return this;
  }

  public CacheBuilder addDecorator(Class<? extends Cache> decorator) {
    if (decorator != null) {
      this.decorators.add(decorator);
    }
    return this;
  }

  public CacheBuilder size(Integer size) {
    this.size = size;
    return this;
  }

  public CacheBuilder clearInterval(Long clearInterval) {
    this.clearInterval = clearInterval;
    return this;
  }

  public CacheBuilder readWrite(boolean readWrite) {
    this.readWrite = readWrite;
    return this;
  }

  public CacheBuilder blocking(boolean blocking) {
    this.blocking = blocking;
    return this;
  }

  public CacheBuilder properties(Properties properties) {
    this.properties = properties;
    return this;
  }
  /**
   1.设置默认的缓存类型及装饰器
   2.应用装饰器到 PerpetualCache 对象上
     遍历装饰器类型集合，并通过反射创建装饰器实例
     将属性设置到实例中
   3.应用一些标准的装饰器
   4.对非 LoggingCache 类型的缓存应用 LoggingCache 装饰器
  */
  // 构建缓存实例
  // 这个方法就是构造者生成产品的具体方法 返回的Cahce对象就是产品角色
  public Cache build() {
    // 设置默认的缓存类型（PerpetualCache）和 缓存装饰器（LruCache）
    setDefaultImplementations();
    // 通过反射创建缓存 // 创建底层Cache实例，底层Cache有个Id
    Cache cache = newBaseCacheInstance(implementation, id);
    setCacheProperties(cache);
    // issue #352, do not apply decorators to custom caches // 仅对内置缓存 PerpetualCache 应用装饰器
    // 自定义的cache不再进行任何的装饰
    if (PerpetualCache.class.equals(cache.getClass())) {
      // 遍历装饰器集合，应用装饰器 // 配置的包装器(淘汰策略的包装器就在decorators中)
      for (Class<? extends Cache> decorator : decorators) {
        // 通过反射创建装饰器实例
        cache = newCacheDecoratorInstance(decorator, cache);
        // 应用标准的装饰器，比如 LoggingCache、 SynchronizedCache
        setCacheProperties(cache);
      }
      // 为 Cache 添加装饰器  // 基本包装器
      cache = setStandardDecorators(cache);
    } else if (!LoggingCache.class.isAssignableFrom(cache.getClass())) {
      // 应用具有日志功能的缓存装饰器
      cache = new LoggingCache(cache);
    }
    // 返回最终缓存的责任链对象
    return cache;
  }

  private void setDefaultImplementations() {
    if (implementation == null) {
      implementation = PerpetualCache.class;
      if (decorators.isEmpty()) {
        decorators.add(LruCache.class);
      }
    }
  }

  // 添加装饰器 装饰模式具体实现
  private Cache setStandardDecorators(Cache cache) {
    try {
      MetaObject metaCache = SystemMetaObject.forObject(cache);
      if (size != null && metaCache.hasSetter("size")) {
        metaCache.setValue("size", size);
      }
      // 添加 ScheduledCache 装饰器 // 当clearInterval设置了值时，就用ScheduledCache进行装饰
      if (clearInterval != null) {
        cache = new ScheduledCache(cache);
        ((ScheduledCache) cache).setClearInterval(clearInterval);
      }
      // 添加 SerializedCache 装饰器  // readOnly设置为false时，使用SerializedCache进行装饰
      if (readWrite) {
        cache = new SerializedCache(cache);
      }
      // 添加 LoggingCache 装饰器 // 使用LoggingCache进行装饰
      cache = new LoggingCache(cache);
      // 添加  SynchronizedCache 装饰器，保证线程安全 // 使用SynchronizedCache进行装饰
      cache = new SynchronizedCache(cache);
      // 如果blocking属性设置为true时，使用BlockingCache进行装饰
      if (blocking) {
        // 添加 BlockingCache 装饰器
        cache = new BlockingCache(cache);
      }
      return cache;
    } catch (Exception e) {
      throw new CacheException("Error building standard cache decorators.  Cause: " + e, e);
    }
  }

  private void setCacheProperties(Cache cache) {
    if (properties != null) {
      MetaObject metaCache = SystemMetaObject.forObject(cache);
      for (Map.Entry<Object, Object> entry : properties.entrySet()) {
        String name = (String) entry.getKey();
        String value = (String) entry.getValue();
        if (metaCache.hasSetter(name)) {
          Class<?> type = metaCache.getSetterType(name);
          if (String.class == type) {
            metaCache.setValue(name, value);
          } else if (int.class == type || Integer.class == type) {
            metaCache.setValue(name, Integer.valueOf(value));
          } else if (long.class == type|| Long.class == type) {
            metaCache.setValue(name, Long.valueOf(value));
          } else if (short.class == type|| Short.class == type) {
            metaCache.setValue(name, Short.valueOf(value));
          } else if (byte.class == type|| Byte.class == type) {
            metaCache.setValue(name, Byte.valueOf(value));
          } else if (float.class == type || Float.class == type) {
            metaCache.setValue(name, Float.valueOf(value));
          } else if (boolean.class == type|| Boolean.class == type) {
            metaCache.setValue(name, Boolean.valueOf(value));
          } else if (double.class == type || Double.class == type) {
            metaCache.setValue(name, Double.valueOf(value));
          } else {
            throw new CacheException("Unsupported property type for cache: '" + name + "' of type " + type);
          }
        }
      }
    }
    if (InitializingObject.class.isAssignableFrom(cache.getClass())) {
      try {
        ((InitializingObject) cache).initialize();
      } catch (Exception e) {
        throw new CacheException("Failed cache initialization for '" + cache.getId() + "' on '" + cache.getClass().getName() + "'", e);
      }
    }
  }

  private Cache newBaseCacheInstance(Class<? extends Cache> cacheClass, String id) {
    Constructor<? extends Cache> cacheConstructor = getBaseCacheConstructor(cacheClass);
    try {
      return cacheConstructor.newInstance(id); // 使用反射获取指定构造函数 来创建类对象
    } catch (Exception e) {
      throw new CacheException("Could not instantiate cache implementation (" + cacheClass + "). Cause: " + e, e);
    }
  }

  private Constructor<? extends Cache> getBaseCacheConstructor(Class<? extends Cache> cacheClass) {
    try {
      return cacheClass.getConstructor(String.class); // 获取指定构造函数
    } catch (Exception e) {
      throw new CacheException("Invalid base cache implementation (" + cacheClass + "). Base cache implementations must have a constructor that takes a String id as a parameter.  Cause: " + e, e);
    }
  }

  private Cache newCacheDecoratorInstance(Class<? extends Cache> cacheClass, Cache base) {
    Constructor<? extends Cache> cacheConstructor = getCacheDecoratorConstructor(cacheClass);
    try {
      return cacheConstructor.newInstance(base);
    } catch (Exception e) {
      throw new CacheException("Could not instantiate cache decorator (" + cacheClass + "). Cause: " + e, e);
    }
  }

  private Constructor<? extends Cache> getCacheDecoratorConstructor(Class<? extends Cache> cacheClass) {
    try {
      return cacheClass.getConstructor(Cache.class);
    } catch (Exception e) {
      throw new CacheException("Invalid cache decorator (" + cacheClass + "). Cache decorators must have a constructor that takes a Cache instance as a parameter.  Cause: " + e, e);

    }
  }
}
