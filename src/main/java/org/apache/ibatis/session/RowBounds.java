
package org.apache.ibatis.session;

/**
 Mybatis提供了一个简单的逻辑分页使用类RowBounds（物理分页当然就是我们在sql语句中指定limit和offset值），
 在DefaultSqlSession提供的某些查询接口中我们可以看到RowBounds是作为参数用来进行分页的，如下接口：
 public <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds)
*/
public class RowBounds {

  /* 默认offset是0**/
  public static final int NO_ROW_OFFSET = 0;

  /* 默认Limit是int的最大值，因此它使用的是逻辑分页**/
  public static final int NO_ROW_LIMIT = Integer.MAX_VALUE;
  public static final RowBounds DEFAULT = new RowBounds();

  private final int offset;
  private final int limit;

  public RowBounds() {
    this.offset = NO_ROW_OFFSET;
    this.limit = NO_ROW_LIMIT;
  }

  public RowBounds(int offset, int limit) {
    this.offset = offset;
    this.limit = limit;
  }

  public int getOffset() {
    return offset;
  }

  public int getLimit() {
    return limit;
  }
}
