
package org.apache.ibatis.cache;

import org.apache.ibatis.cache.decorators.SerializedCache;
import org.apache.ibatis.cache.decorators.WeakCache;
import org.apache.ibatis.cache.impl.PerpetualCache;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WeakCacheTest {

  Cache cache = new WeakCache(new PerpetualCache("default"));

  @Test
  void shouldDemonstrateObjectsBeingCollectedAsNeeded() {
    final int N = 3000000;

    for (int i = 0; i < N; i++) {
      cache.putObject(i, i);
      if (cache.getSize() < i + 1) {
        // System.out.println("Cache exceeded with " + (i + 1) + " entries.");
        break;
      }
      if ((i + 1) % 100000 == 0) {
        // Try performing GC.
        System.gc();
      }
    }
    assertTrue(cache.getSize() < N);
  }

  @Test
  void shouldDemonstrateCopiesAreEqual() {
    cache = new SerializedCache(cache);
    for (int i = 0; i < 1000; i++) {
      cache.putObject(i, i);
      Object value = cache.getObject(i);
      assertTrue(value == null || value.equals(i));
    }
  }

  @Test
  void shouldRemoveItemOnDemand() {
    cache.putObject(0, 0);
    assertNotNull(cache.getObject(0));
    cache.removeObject(0);
    assertNull(cache.getObject(0));
  }

  @Test
  void shouldFlushAllItemsOnDemand() {
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
