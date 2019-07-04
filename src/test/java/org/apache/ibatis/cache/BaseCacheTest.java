
package org.apache.ibatis.cache;

import org.apache.ibatis.cache.decorators.LoggingCache;
import org.apache.ibatis.cache.decorators.ScheduledCache;
import org.apache.ibatis.cache.decorators.SerializedCache;
import org.apache.ibatis.cache.decorators.SynchronizedCache;
import org.apache.ibatis.cache.impl.PerpetualCache;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

class BaseCacheTest {

  @Test
  void shouldDemonstrateEqualsAndHashCodeForVariousCacheTypes() {
    PerpetualCache cache = new PerpetualCache("test_cache");
    assertEquals(cache, cache);
    assertEquals(cache, new SynchronizedCache(cache));
    assertEquals(cache, new SerializedCache(cache));
    assertEquals(cache, new LoggingCache(cache));
    assertEquals(cache, new ScheduledCache(cache));

    assertEquals(cache.hashCode(), new SynchronizedCache(cache).hashCode());
    assertEquals(cache.hashCode(), new SerializedCache(cache).hashCode());
    assertEquals(cache.hashCode(), new LoggingCache(cache).hashCode());
    assertEquals(cache.hashCode(), new ScheduledCache(cache).hashCode());

    Set<Cache> caches = new HashSet<>();
    caches.add(cache);
    caches.add(new SynchronizedCache(cache));
    caches.add(new SerializedCache(cache));
    caches.add(new LoggingCache(cache));
    caches.add(new ScheduledCache(cache));
    assertEquals(1, caches.size());
  }

}
