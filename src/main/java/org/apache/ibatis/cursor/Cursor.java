
package org.apache.ibatis.cursor;

import java.io.Closeable;

/**
 * Cursor contract to handle fetching items lazily using an Iterator.
 * Cursors are a perfect fit to handle millions of items queries that would not normally fits in memory.
 * If you use collections in resultMaps then cursor SQL queries must be ordered (resultOrdered="true")
 * using the id columns of the resultMap.
 *
 * @author Guillaume Darmont / guillaume@dropinocean.com
 */
public interface Cursor<T> extends Closeable, Iterable<T> {

  /**
   * @return true if the cursor has started to fetch items from database.
   */
  boolean isOpen();

  /**
   *
   * @return true if the cursor is fully consumed and has returned all elements matching the query.
   */
  boolean isConsumed();

  /**
   * Get the current item index. The first item has the index 0.
   * @return -1 if the first cursor item has not been retrieved. The index of the current item retrieved.
   */
  int getCurrentIndex();
}
