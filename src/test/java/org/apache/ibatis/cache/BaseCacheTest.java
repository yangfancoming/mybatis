
package org.apache.ibatis.cache;

import org.apache.ibatis.cache.decorators.LoggingCache;
import org.apache.ibatis.cache.decorators.ScheduledCache;
import org.apache.ibatis.cache.decorators.SerializedCache;
import org.apache.ibatis.cache.decorators.SynchronizedCache;
import org.apache.ibatis.cache.impl.PerpetualCache;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class BaseCacheTest {

  // 以下五种缓存，他们的 Equals And HashCode结果都相同，是因为他们重写了Object类的 hashCode和equals方法。。。
  PerpetualCache cache = new PerpetualCache("test_cache");
  SynchronizedCache synchronizedCache = new SynchronizedCache(cache);
  SerializedCache serializedCache = new SerializedCache(cache);
  LoggingCache loggingCache = new LoggingCache(cache);
  ScheduledCache scheduledCache = new ScheduledCache(cache);

  // 证明：对于不同的实现类型 他们的 Equals And HashCode 都是相同的
  @Test
  void shouldDemonstrateEqualsAndHashCodeForVariousCacheTypes() {
    // 如果两个对象的equals方法相等，那么hashcode方法也一定相等，反之 如果两个对象的hashcode方法相当，那么equals方法却不一定相等
    // 对比 Equals
    assertEquals(cache, cache);
    assertEquals(cache,synchronizedCache);
    assertEquals(cache, serializedCache);
    assertEquals(cache, loggingCache);
    assertEquals(cache, scheduledCache);

    // 对比 HashCode
    assertEquals(cache.hashCode(), synchronizedCache.hashCode());
    assertEquals(cache.hashCode(), serializedCache.hashCode());
    assertEquals(cache.hashCode(), loggingCache.hashCode());
    assertEquals(cache.hashCode(), scheduledCache.hashCode());
  }

  /**
   * 五个地址都是一样的！
   * org.apache.ibatis.cache.impl.PerpetualCache@7a7e2195
   * org.apache.ibatis.cache.decorators.SynchronizedCache@7a7e2195
   * org.apache.ibatis.cache.decorators.SerializedCache@7a7e2195
   * org.apache.ibatis.cache.decorators.LoggingCache@7a7e2195
   * org.apache.ibatis.cache.decorators.ScheduledCache@7a7e2195
  */
  @Test
  public void test1(){
    System.out.println(cache);
    System.out.println(synchronizedCache);
    System.out.println(serializedCache);
    System.out.println(loggingCache);
    System.out.println(scheduledCache);
  }

  @Test
  public void test2(){
    assertTrue(synchronizedCache.equals(serializedCache));
    assertTrue(serializedCache.equals(scheduledCache));
  }

  // set 集合特点： 不能重复
  @Test
  public void test(){
    Set<Cache> caches = new HashSet<>();
    caches.add(cache);
    caches.add(synchronizedCache);
    caches.add(serializedCache);
    caches.add(loggingCache);
    caches.add(scheduledCache);
    assertEquals(1, caches.size());
  }

}
