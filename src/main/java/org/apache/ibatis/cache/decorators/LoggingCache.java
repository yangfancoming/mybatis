
package org.apache.ibatis.cache.decorators;

import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

/**
 LoggingCache 在 Cache 的基础上提供了日志功能
 输出缓存命中的日志信息
*/
public class LoggingCache implements Cache {

  private final Log log;
  private final Cache delegate;
  // 缓存请求次数
  protected int requests = 0;
  // 缓存命中次数
  protected int hits = 0;

  public LoggingCache(Cache delegate) {
    this.delegate = delegate;
    this.log = LogFactory.getLog(getId());
  }

  @Override
  public String getId() {
    return delegate.getId();
  }

  @Override
  public int getSize() {
    return delegate.getSize();
  }

  @Override
  public void putObject(Object key, Object object) {
    delegate.putObject(key, object);
  }

  // 统计命中次数和访问次数 这两个指标，井按照指定的日志输出方式输出命中率。
  @Override
  public Object getObject(Object key) {
    requests++;
    final Object value = delegate.getObject(key);
    if (value != null)  hits++;
    if (log.isDebugEnabled()) log.debug("Cache Hit Ratio [" + getId() + "]: " + getHitRatio());
    return value;
  }

  @Override
  public Object removeObject(Object key) {
    return delegate.removeObject(key);
  }

  @Override
  public void clear() {
    delegate.clear();
  }

  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return delegate.equals(obj);
  }

  private double getHitRatio() {
    return (double) hits / (double) requests;
  }

}
