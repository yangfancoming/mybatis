
package org.apache.ibatis.cache;

import org.apache.ibatis.cache.decorators.LoggingCache;
import org.apache.ibatis.cache.decorators.ScheduledCache;
import org.apache.ibatis.cache.impl.PerpetualCache;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class ScheduledCacheTest {

  Cache cache = new ScheduledCache(new PerpetualCache("DefaultCache")); // 装饰定时清理

  @Test
  public void tst() throws InterruptedException {
    String key = "goat";
    String value = "heihei";
    ((ScheduledCache) cache).setClearInterval(2500); // 设定2.5秒 后清除缓存
    cache.putObject(key,value);
    assertEquals(value, cache.getObject(key));
    TimeUnit.SECONDS.sleep(3);// 线程睡眠3秒
    assertNull(cache.getObject(key));
  }

  /*  演示如何根据时间清空所有对象  */
  @Test
  void shouldDemonstrateHowAllObjectsAreFlushedAfterBasedOnTime() throws Exception {
    ((ScheduledCache) cache).setClearInterval(2500); // 设定2.5秒 后清除缓存
    cache = new LoggingCache(cache); // 装饰日志打印
    for (int i = 0; i < 100; i++) {
      cache.putObject(i, i);
      assertEquals(i, cache.getObject(i));
    }
    Thread.sleep(5000);
    assertEquals(0, cache.getSize());
  }

  @Test
  void shouldRemoveItemOnDemand() {
    ((ScheduledCache) cache).setClearInterval(60);
    cache = new LoggingCache(cache);
    cache.putObject(0, 0);
    assertNotNull(cache.getObject(0));
    cache.removeObject(0);
    assertNull(cache.getObject(0));
  }

  @Test
  void shouldFlushAllItemsOnDemand() {
    ((ScheduledCache) cache).setClearInterval(60);
    cache = new LoggingCache(cache);
    for (int i = 0; i < 5; i++) {
      cache.putObject(i, i);
    }
    assertNotNull(cache.getObject(0));
    assertNotNull(cache.getObject(4));
    cache.clear();
    assertNull(cache.getObject(0));
    assertNull(cache.getObject(4));
  }

}
