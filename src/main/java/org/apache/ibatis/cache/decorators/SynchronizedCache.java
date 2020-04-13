
package org.apache.ibatis.cache.decorators;

import org.apache.ibatis.cache.Cache;

/**
 来看看 SynchronizedCache 装饰器类吧，在上面的缓存实现中介绍到了 Mybatis 其实就是使用 HashMap 来实现缓存的，
 即把数据放入到 HashMap中，但是 HashMap 不是线安全的，
 Mybatis 是如何来保证缓存中的线程安全问题呢？就是使用了 SynchronizedCache 来保证的，它是一个装饰器类，其中的方法都加上了 synchronized 关键字：
*/
public class SynchronizedCache implements Cache {

  private final Cache delegate;

  public SynchronizedCache(Cache delegate) {
    this.delegate = delegate;
  }

  //---------------------------------------------------------------------
  // Implementation of 【Cache】 interface
  //---------------------------------------------------------------------

  @Override
  public String getId() {
    return delegate.getId();
  }

  @Override
  public synchronized int getSize() {
    return delegate.getSize();
  }

  @Override
  public synchronized void putObject(Object key, Object object) {
    delegate.putObject(key, object);
  }

  @Override
  public synchronized Object getObject(Object key) {
    return delegate.getObject(key);
  }

  @Override
  public synchronized Object removeObject(Object key) {
    return delegate.removeObject(key);
  }

  @Override
  public synchronized void clear() {
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
