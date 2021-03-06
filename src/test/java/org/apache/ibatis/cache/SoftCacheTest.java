
package org.apache.ibatis.cache;

import org.apache.ibatis.cache.decorators.SerializedCache;
import org.apache.ibatis.cache.decorators.SoftCache;
import org.apache.ibatis.cache.impl.PerpetualCache;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SoftCacheTest {

  Cache cache = new SoftCache(new PerpetualCache("default"));

  @Test
  void shouldDemonstrateObjectsBeingCollectedAsNeeded() {
    final int N = 3000000;
    for (int i = 0; i < N; i++) {
      byte[] array = new byte[5001]; //waste a bunch of memory
      array[5000] = 1;
      cache.putObject(i, array);
      Object value = cache.getObject(i);
      if (cache.getSize() < i + 1) {
        System.out.println("Cache exceeded with " + (i + 1) + " entries.");
        break;
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
