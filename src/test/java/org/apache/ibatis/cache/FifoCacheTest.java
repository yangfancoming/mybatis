
package org.apache.ibatis.cache;

import org.apache.ibatis.cache.decorators.FifoCache;
import org.apache.ibatis.cache.impl.PerpetualCache;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

// 先进先出
class FifoCacheTest {

  FifoCache cache = new FifoCache(new PerpetualCache("default"));

  /* 超出5个元素后 将删除最先进来元素 */
  @Test
  void shouldRemoveFirstItemInBeyondFiveEntries() {
    // 设置缓存队列容量
    cache.setSize(5);
    // 按顺序 从0到4放入数据到缓存队列中
    for (int i = 0; i < 5; i++) {
      cache.putObject(i, i);
    }
    assertEquals(0, cache.getObject(0));
    // 再次添加缓存 由于容量已满 将最先进来的 0 元素顶掉
    cache.putObject(5, 5);
    assertNull(cache.getObject(0)); // 断言 0 元素已被删除
    assertEquals(5, cache.getSize());
  }

  /* 按需删除指定缓存项  */
  @Test
  void shouldRemoveItemOnDemand() {
    cache.putObject(0, 0);
    assertNotNull(cache.getObject(0));
    cache.removeObject(0);
    assertNull(cache.getObject(0));
  }

  /* 按需删除所有缓存项目  */
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
    assertEquals(0,cache.getSize());
  }

}
