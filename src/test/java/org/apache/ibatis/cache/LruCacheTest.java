
package org.apache.ibatis.cache;

import org.apache.ibatis.cache.decorators.LruCache;
import org.apache.ibatis.cache.impl.PerpetualCache;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

//Least Recently Used  最少使用频率
class LruCacheTest {

  PerpetualCache perpetualCache = new PerpetualCache("default");

  @Test
  void shouldRemoveLeastRecentlyUsedItemInBeyondFiveEntries() {
    LruCache cache = new LruCache(perpetualCache);
    cache.setSize(5);
    for (int i = 0; i < 5; i++) {
      cache.putObject(i, i);
    }
    // 本应该先删除先进入的0元素  但是这里使用了一次 0元素  导致 按照最少使用频率不会删除 0元素 而是会删除 1元素
    assertEquals(0, cache.getObject(0)); //
    cache.putObject(5, 5);
    assertNull(cache.getObject(1));
    assertEquals(5, cache.getSize());
  }

  @Test
  void shouldRemoveItemOnDemand() {
    Cache cache = new LruCache(perpetualCache);
    cache.putObject(0, 0);
    assertNotNull(cache.getObject(0));
    cache.removeObject(0);
    assertNull(cache.getObject(0));
  }

  @Test
  void shouldFlushAllItemsOnDemand() {
    Cache cache = new LruCache(perpetualCache);
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
