
package org.apache.ibatis.cache.decorators;

import org.apache.ibatis.cache.Cache;

/**
 * 周期性清理缓存的装饰器
 缓存的清理策略是定时处理，（定时清理全部缓存）.感觉这个没啥大作用，Guava 基于定时清理部分缓存的实现比较实用.
*/
public class ScheduledCache implements Cache {

  private final Cache delegate;
  // 两次缓存清理之间的时间间隔  默认是一小时，
  protected long clearInterval;
  // 最后一次清理时间
  protected long lastClear;

  public ScheduledCache(Cache delegate) {
    this.delegate = delegate;
    this.clearInterval = 60 * 60 * 1000; // 1 hour
    this.lastClear = System.currentTimeMillis();
  }

  public void setClearInterval(long clearInterval) {
    this.clearInterval = clearInterval;
  }

  private boolean clearWhenStale() {
    if (System.currentTimeMillis() - lastClear > clearInterval) {
      clear();
      return true;
    }
    return false;
  }

  //---------------------------------------------------------------------
  // Implementation of 【Cache】 interface
  //---------------------------------------------------------------------
  @Override
  public String getId() {
    return delegate.getId();
  }

  @Override
  public int getSize() {
    clearWhenStale();
    return delegate.getSize();
  }

  @Override
  public void putObject(Object key, Object object) {
    clearWhenStale();
    delegate.putObject(key, object);
  }

  @Override
  public Object getObject(Object key) {
    return clearWhenStale() ? null : delegate.getObject(key);
  }

  @Override
  public Object removeObject(Object key) {
    clearWhenStale();
    return delegate.removeObject(key);
  }

  @Override
  public void clear() {
    lastClear = System.currentTimeMillis();
    delegate.clear();
  }

  //---------------------------------------------------------------------
  // Implementation of 【Object】 class
  //---------------------------------------------------------------------
  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return delegate.equals(obj);
  }

}
