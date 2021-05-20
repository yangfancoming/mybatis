
package org.apache.ibatis.cache;

import org.apache.ibatis.reflection.ArrayUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 Mybatis 的缓存使用了 key-value 的形式存入到 HashMap 中，而 key 的话，Mybatis 使用了 CacheKey 来表示 key，它的生成规则为：mappedStementId + offset + limit + SQL + queryParams + environment生成一个哈希码.
*/
public class CacheKey implements Cloneable, Serializable {

  private static final long serialVersionUID = 1146682552656046210L;
  public static final CacheKey NULL_CACHE_KEY = new NullCacheKey();
  private static final int DEFAULT_MULTIPLYER = 37;
  private static final int DEFAULT_HASHCODE = 17;

  // 参与计算hashcode，默认值为37
  private final int multiplier;
  // CacheKey 对象的 hashcode ，默认值 17
  private int hashcode;
  // 检验和
  private long checksum;
  // updateList 集合的个数
  private int count;
  // 8/21/2017 - Sonarlint flags this as needing to be marked transient.  While true if content is not serializable, this is not always true and thus should not be marked transient.
  // 由该集合中的所有对象来共同决定两个 CacheKey 是否相等
  private List<Object> updateList;

  public CacheKey() {
    this.hashcode = DEFAULT_HASHCODE;
    this.multiplier = DEFAULT_MULTIPLYER;
    this.count = 0;
    this.updateList = new ArrayList<>();
  }

  public CacheKey(Object[] objects) {
    this();
    updateAll(objects);
  }

  public int getUpdateCount() {
    return updateList.size();
  }

  // 调用该方法，向 updateList 集合添加对应的对象
  public void update(Object object) {
    int baseHashCode = object == null ? 1 : ArrayUtil.hashCode(object);
    count++;
    checksum += baseHashCode;
    baseHashCode *= count;
    hashcode = multiplier * hashcode + baseHashCode;
    updateList.add(object);
  }

  public void updateAll(Object[] objects) {
    for (Object o : objects) {
      update(o);
    }
  }

  //---------------------------------------------------------------------
  // Implementation of 【Object】 class
  //---------------------------------------------------------------------
  /**
   * CacheKey#equals
   *  缓存key的设计包含命名空间，分页信息，sql语句 ，参数值的信息，在BaseExecutor#createCacheKey这里是创建cacheKey的方法，包含了上面的四个要素。
   * 判断2个key相等的逻辑
   * 1.是一个对象，那么返回true
   * 2.被比较对象类型不是CacheKey，直接返回false
   * 3.hashcode不一样，返回false
   * 4.checksum不一样，返回false
   * 5.count不一样，返回false
   * 6.updateList列表不一样，返回false
   * 7.满足上面的条件，返回true，认为两个缓存key是一样的
   */
  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (!(object instanceof CacheKey)) {
      return false;
    }
    final CacheKey cacheKey = (CacheKey) object;
    if (hashcode != cacheKey.hashcode) return false;
    if (checksum != cacheKey.checksum) return false;
    if (count != cacheKey.count)       return false;
    // 如果前几项都不满足，则循环遍历 updateList 集合，判断每一项是否相等，如果有一项不相等则这两个CacheKey不相等
    for (int i = 0; i < updateList.size(); i++) {
      Object thisObject = updateList.get(i);
      Object thatObject = cacheKey.updateList.get(i);
      if (!ArrayUtil.equals(thisObject, thatObject)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public int hashCode() {
    return hashcode;
  }

  @Override
  public String toString() {
    StringJoiner returnValue = new StringJoiner(":");
    returnValue.add(String.valueOf(hashcode));
    returnValue.add(String.valueOf(checksum));
    updateList.stream().map(ArrayUtil::toString).forEach(returnValue::add);
    return returnValue.toString();
  }

  @Override
  public CacheKey clone() throws CloneNotSupportedException {
    CacheKey clonedCacheKey = (CacheKey) super.clone();
    clonedCacheKey.updateList = new ArrayList<>(updateList);
    return clonedCacheKey;
  }
}
