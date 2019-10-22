
package org.apache.ibatis.cursor.defaults;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.resultset.DefaultResultSetHandler;
import org.apache.ibatis.executor.resultset.ResultSetWrapper;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

/**
 * This is the default implementation of a MyBatis Cursor. This implementation is not thread safe.
 */
public class DefaultCursor<T> implements Cursor<T> {

  // ResultSetHandler stuff
  private final DefaultResultSetHandler resultSetHandler;
  private final ResultMap resultMap;
  private final ResultSetWrapper rsw;
  private final RowBounds rowBounds;
  private final ObjectWrapperResultHandler<T> objectWrapperResultHandler = new ObjectWrapperResultHandler<>();

  private final CursorIterator cursorIterator = new CursorIterator();
  private boolean iteratorRetrieved;

  private CursorStatus status = CursorStatus.CREATED;
  private int indexWithRowBound = -1;

  private enum CursorStatus {

    /**
     * A freshly created cursor, database ResultSet consuming has not started.
     */
    CREATED,
    /**
     * A cursor currently in use, database ResultSet consuming has started.
     */
    OPEN,
    /**
     * A closed cursor, not fully consumed.
     */
    CLOSED,
    /**
     * A fully consumed cursor, a consumed cursor is always closed.
     */
    CONSUMED
  }

  public DefaultCursor(DefaultResultSetHandler resultSetHandler, ResultMap resultMap, ResultSetWrapper rsw, RowBounds rowBounds) {
    this.resultSetHandler = resultSetHandler;
    this.resultMap = resultMap;
    this.rsw = rsw;
    this.rowBounds = rowBounds;
  }

  @Override
  public boolean isOpen() {
    return status == CursorStatus.OPEN;
  }

  @Override
  public boolean isConsumed() {
    return status == CursorStatus.CONSUMED;
  }

  @Override
  public int getCurrentIndex() {
    return rowBounds.getOffset() + cursorIterator.iteratorIndex;
  }

  @Override
  public Iterator<T> iterator() {
    if (iteratorRetrieved) {
      throw new IllegalStateException("Cannot open more than one iterator on a Cursor");
    }
    if (isClosed()) {
      throw new IllegalStateException("A Cursor is already closed.");
    }
    iteratorRetrieved = true;
    return cursorIterator;
  }

  @Override
  public void close() {
    if (isClosed()) {
      return;
    }

    ResultSet rs = rsw.getResultSet();
    try {
      if (rs != null) {
        rs.close();
      }
    } catch (SQLException e) {
      // ignore
    } finally {
      status = CursorStatus.CLOSED;
    }
  }

  protected T fetchNextUsingRowBound() {
    T result = fetchNextObjectFromDatabase();
    while (result != null && indexWithRowBound < rowBounds.getOffset()) {
      result = fetchNextObjectFromDatabase();
    }
    return result;
  }

  protected T fetchNextObjectFromDatabase() {
    if (isClosed()) {
      return null;
    }

    try {
      status = CursorStatus.OPEN;
      if (!rsw.getResultSet().isClosed()) {
        resultSetHandler.handleRowValues(rsw, resultMap, objectWrapperResultHandler, RowBounds.DEFAULT, null);
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

    T next = objectWrapperResultHandler.result;
    if (next != null) {
      indexWithRowBound++;
    }
    // No more object or limit reached
    if (next == null || getReadItemsCount() == rowBounds.getOffset() + rowBounds.getLimit()) {
      close();
      status = CursorStatus.CONSUMED;
    }
    objectWrapperResultHandler.result = null;

    return next;
  }

  private boolean isClosed() {
    return status == CursorStatus.CLOSED || status == CursorStatus.CONSUMED;
  }

  private int getReadItemsCount() {
    return indexWithRowBound + 1;
  }

  private static class ObjectWrapperResultHandler<T> implements ResultHandler<T> {

    private T result;

    @Override
    public void handleResult(ResultContext<? extends T> context) {
      this.result = context.getResultObject();
      context.stop();
    }
  }

  private class CursorIterator implements Iterator<T> {

    /**
     * Holder for the next object to be returned.
     */
    T object;

    /**
     * Index of objects returned using next(), and as such, visible to users.
     */
    int iteratorIndex = -1;

    @Override
    public boolean hasNext() {
      if (object == null) {
        object = fetchNextUsingRowBound();
      }
      return object != null;
    }

    @Override
    public T next() {
      // Fill next with object fetched from hasNext()
      T next = object;

      if (next == null) {
        next = fetchNextUsingRowBound();
      }

      if (next != null) {
        object = null;
        iteratorIndex++;
        return next;
      }
      throw new NoSuchElementException();
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException("Cannot remove element from Cursor");
    }
  }
}
