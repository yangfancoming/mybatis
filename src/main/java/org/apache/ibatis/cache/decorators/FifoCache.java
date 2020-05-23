
package org.apache.ibatis.cache.decorators;

import java.util.Deque;
import java.util.LinkedList;

import org.apache.ibatis.cache.Cache;

/**
 * FIFO (first in, first out) cache decorator.
 * 给缓存添加淘汰策略，实现FIFO的功能
 * 1.我们发现FifoCache也实现了Cache接口，因此它的行为和PerpetualCache几乎是一致的(都实现了Cache接口定义的方法)
 * 并且FifoCache的这些方法的实现，内部都是调用PerpetualCache的实现，因为PerpetualCache才是缓存功能真正的实现者嘛。
 * 2.但是FifoCache也有自己需要添加的功能，需要实现FIFO的功能，因此FifoCache内部维护着一个FIFO队列的keyList，队列里
 * 保存着缓存的数据的key，在每一次保存数据的时候都会检查FIFO队列对起进行维护，超过设定大小就会去除最旧的key，同时把
 * 缓存中的key也删除。这样就为缓存实现了FIFO的淘汰策略
 * 小结一下：
 * 为线程添加缓存FIFO淘汰功能修改的地方只有一下2处，其他都是原封不动的调用PerpetualCache的方法：
 * A.存入元素时，需要检查FIFO队列
 * B.清理缓存时，清除FIFO队列
 */
public class FifoCache implements Cache {

  // 真正的缓存功能实现对象
  private final Cache delegate;

  // FIFO队列
  private final Deque<Object> keyList;

  // 队列大小，默认1024
  private int size;

  public FifoCache(Cache delegate) {
    this.delegate = delegate;
    this.keyList = new LinkedList<>();
    this.size = 1024;
  }

  private void cycleKeyList(Object key) {
    // 新添加的key需要保存到keyList里面
    keyList.addLast(key);
    // 判断是否到了最大长度，到了的话需要移除头部最旧的key，同时把缓存中对应的key也删除掉
    if (keyList.size() > size) {
      Object oldestKey = keyList.removeFirst();
      delegate.removeObject(oldestKey);
    }
  }

  public void setSize(int size) {
    this.size = size;
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
    return delegate.getSize();
  }

  @Override
  public void putObject(Object key, Object value) {
    // 存入元素时,检查FIFO队列
    cycleKeyList(key);
    delegate.putObject(key, value);
  }

  @Override
  public Object getObject(Object key) {
    return delegate.getObject(key);
  }

  @Override
  public Object removeObject(Object key) {
    return delegate.removeObject(key);
  }

  @Override
  public void clear() {
    // 清理操作时，将FIFO队列也清除
    delegate.clear();
    keyList.clear();
  }

}
