
package org.apache.ibatis.cache;

import org.apache.ibatis.cache.decorators.LoggingCache;
import org.apache.ibatis.cache.decorators.ScheduledCache;
import org.apache.ibatis.cache.impl.PerpetualCache;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class ScheduledCacheTest {

  Cache cache = new PerpetualCache("DefaultCache");

  @Test
  void shouldDemonstrateHowAllObjectsAreFlushedAfterBasedOnTime() throws Exception {
    cache = new ScheduledCache(cache);
    ((ScheduledCache) cache).setClearInterval(2500);
    cache = new LoggingCache(cache);
    for (int i = 0; i < 100; i++) {
      cache.putObject(i, i);
      assertEquals(i, cache.getObject(i));
    }
    Thread.sleep(5000);
    assertEquals(0, cache.getSize());
  }

  @Test
  void shouldRemoveItemOnDemand() {

    cache = new ScheduledCache(cache);
    ((ScheduledCache) cache).setClearInterval(60000);
    cache = new LoggingCache(cache);
    cache.putObject(0, 0);
    assertNotNull(cache.getObject(0));
    cache.removeObject(0);
    assertNull(cache.getObject(0));
  }

  @Test
  void shouldFlushAllItemsOnDemand() {

    cache = new ScheduledCache(cache);
    ((ScheduledCache) cache).setClearInterval(60000);
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
