
package org.apache.ibatis.cache;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

class CacheKeyTest {

  // 测试两个 CacheKey 相同
  @Test
  void shouldTestCacheKeysEqual() {
    Date date = new Date();
    System.out.println(date.getTime()); // t1
    CacheKey key1 = new CacheKey(new Object[] { 1, "hello", null, new Date(date.getTime()) });
    CacheKey key2 = new CacheKey(new Object[] { 1, "hello", null, new Date(date.getTime()) });
    assertEquals(key1, key2);
    assertEquals(key2, key1);
    assertEquals(key1.hashCode(), key2.hashCode());
    assertEquals(key1.toString(), key2.toString());
    System.out.println(date.getTime()); // t2
    // 结果 t1 = t2
  }

  // 测试 由于Date时间不同导致的两个CacheKey 不同
  @Test
  void shouldTestCacheKeysNotEqualDueToDateDifference() throws Exception {
    CacheKey key1 = new CacheKey(new Object[] { 1, "hello", null, new Date() });
    Thread.sleep(1000);
    CacheKey key2 = new CacheKey(new Object[] { 1, "hello", null, new Date() });
    assertNotEquals(key1, key2);
    assertNotEquals(key2, key1);
    assertNotEquals(key1.hashCode(), key2.hashCode());
    assertNotEquals(key1.toString(), key2.toString());
  }

  // 测试由于 参数顺序不同 导致两个CacheKey不同
  @Test
  void shouldTestCacheKeysNotEqualDueToOrder() {
    CacheKey key1 = new CacheKey(new Object[] { 1, "hello", null });
    CacheKey key2 = new CacheKey(new Object[] { 1, null, "hello" });
    assertNotEquals(key1, key2);
    assertNotEquals(key2, key1);
    assertNotEquals(key1.hashCode(), key2.hashCode());
    assertNotEquals(key1.toString(), key2.toString());
  }

  // 测试 空和null的多个CacheKey 都是相同的
  @Test
  void shouldDemonstrateEmptyAndNullKeysAreEqual() {
    CacheKey key1 = new CacheKey();
    CacheKey key2 = new CacheKey();
    assertEquals(key1, key2);
    assertEquals(key2, key1);

    key1.update(null);
    key2.update(null);
    assertEquals(key1, key2);
    assertEquals(key2, key1);
  }

  // 测试 使用字节数组生成 缓存key
  @Test
  void shouldTestCacheKeysWithBinaryArrays() {
    byte[] array1 = new byte[] { 1 };
    byte[] array2 = new byte[] { 1 };
    CacheKey key1 = new CacheKey(new Object[] { array1 });
    CacheKey key2 = new CacheKey(new Object[] { array2 });
    assertEquals(key1, key2);
  }

  @Test
  void serializationExceptionTest() {
    CacheKey cacheKey = new CacheKey();
    cacheKey.update(new Object());
    Assertions.assertThrows(NotSerializableException.class, () -> serialize(cacheKey));
  }

  @Test
  void serializationTest() throws Exception {
    CacheKey cacheKey = new CacheKey();
    cacheKey.update("serializable");
    Assertions.assertEquals(cacheKey, serialize(cacheKey));
  }

  private static <T> T serialize(T object) throws Exception {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      new ObjectOutputStream(baos).writeObject(object);

      ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
      return (T) new ObjectInputStream(bais).readObject();
  }

}
