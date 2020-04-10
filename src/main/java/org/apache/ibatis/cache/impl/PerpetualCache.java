
package org.apache.ibatis.cache.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.cache.CacheException;

/**
 Mybatis 为 Cache 接口提供的唯一一个实现类就是 PerpetualCache，
 因为 PerpetualCache 类中并没有   private final Cache delegate;  成员变量
 这个唯一并不是说 Cache 只有一个实现类，只是缓存的处理逻辑，
 Cache 还有其他的实现类，但是只是作为装饰器存在，只是对 Cache 进行包装而已。
*/
public class PerpetualCache implements Cache {

  // id,一般对应mapper.xml 的namespace 的值
  private final String id;

  // 用来存放数据，即缓存底层就是使用 map 来实现的
  private Map<Object, Object> cache = new HashMap<>();

  public PerpetualCache(String id) {
    this.id = id;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public int getSize() {
    return cache.size();
  }

  @Override
  public void putObject(Object key, Object value) {
    cache.put(key, value);
  }

  @Override
  public Object getObject(Object key) {
    return cache.get(key);
  }

  @Override
  public Object removeObject(Object key) {
    return cache.remove(key);
  }

  @Override
  public void clear() {
    cache.clear();
  }

  @Override
  public boolean equals(Object o) {
    if (getId() == null)  throw new CacheException("Cache instances require an ID.");
    if (this == o) return true;
    if (!(o instanceof Cache)) {
      return false;
    }
    Cache otherCache = (Cache) o;
    return getId().equals(otherCache.getId());
  }

  @Override
  public int hashCode() {
    if (getId() == null) throw new CacheException("Cache instances require an ID.");
    return getId().hashCode();
  }

}
