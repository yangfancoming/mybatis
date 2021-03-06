
package org.apache.ibatis.cache;

import org.apache.ibatis.cache.decorators.SerializedCache;
import org.apache.ibatis.cache.decorators.SynchronizedCache;
import org.apache.ibatis.cache.impl.PerpetualCache;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PerpetualCacheTest {

  Cache cache = new PerpetualCache("default");

  // 测试SynchronizedCache缓存
  @Test
  void shouldDemonstrateHowAllObjectsAreKept() {
    cache = new SynchronizedCache(cache);
    for (int i = 0; i < 100; i++) {
      cache.putObject(i, i);
      assertEquals(i, cache.getObject(i));
    }
    assertEquals(100, cache.getSize());
  }

  @Test
  void shouldDemonstrateCopiesAreEqual() {
    cache = new SerializedCache(cache);
    for (int i = 0; i < 10; i++) {
      cache.putObject(i, i);
      assertEquals(i, cache.getObject(i));
    }
  }

  @Test
  void shouldRemoveItemOnDemand() {
    cache = new SynchronizedCache(cache);
    cache.putObject(0, 0);
    assertNotNull(cache.getObject(0));
    cache.removeObject(0);
    assertNull(cache.getObject(0));
  }

  @Test
  void shouldFlushAllItemsOnDemand() {
    cache = new SynchronizedCache(cache);
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
