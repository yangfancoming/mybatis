
package org.apache.ibatis.executor.resultset;

import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultResultSetHandlerTest2 {

  @Spy
  private ImpatientResultSet rs;
  @Mock
  private Statement stmt;
  @Mock
  protected ResultSetMetaData rsmd;
  @Mock
  private Connection conn;
  @Mock
  private DatabaseMetaData dbmd;

  @SuppressWarnings("serial")
  @Test
  void shouldNotCallNextOnClosedResultSet_SimpleResult() throws Exception {
    // 模拟 全局xml配置文件
    final Configuration config = new Configuration();
    final TypeHandlerRegistry registry = config.getTypeHandlerRegistry();
    final MappedStatement ms = new MappedStatement.Builder(config, "testSelect",
      new StaticSqlSource(config, "some select statement"), SqlCommandType.SELECT).resultMaps(
        new ArrayList<ResultMap>() {
          {
            add(new ResultMap.Builder(config, "testMap", HashMap.class, new ArrayList<ResultMapping>() {
              {
                add(new ResultMapping.Builder(config, "id", "id", registry.getTypeHandler(Integer.class)).build());
              }
            }).build());
          }
        }).build();

    final Executor executor = null;
    final ParameterHandler parameterHandler = null;
    final ResultHandler<?> resultHandler = null;
    final BoundSql boundSql = null;
    final RowBounds rowBounds = new RowBounds(5, 1);
    final DefaultResultSetHandler resultSetHandler = new DefaultResultSetHandler(executor, ms, parameterHandler, resultHandler, boundSql, rowBounds);

    when(stmt.getResultSet()).thenReturn(rs);
    when(rsmd.getColumnCount()).thenReturn(1);
    when(rsmd.getColumnLabel(1)).thenReturn("id");
    when(rsmd.getColumnType(1)).thenReturn(Types.INTEGER);
    when(rsmd.getColumnClassName(1)).thenReturn(Integer.class.getCanonicalName());
    when(stmt.getConnection()).thenReturn(conn);
    when(conn.getMetaData()).thenReturn(dbmd);
    when(dbmd.supportsMultipleResultSets()).thenReturn(false); // for simplicity.

    final List<Object> results = resultSetHandler.handleResultSets(stmt);
    assertEquals(0, results.size());
  }

  @SuppressWarnings("serial")
  @Test
  void shouldNotCallNextOnClosedResultSet_NestedResult() throws Exception {
    final Configuration config = new Configuration();
    final TypeHandlerRegistry registry = config.getTypeHandlerRegistry();
    final ResultMap nestedResultMap = new ResultMap.Builder(config, "roleMap", HashMap.class,
      new ArrayList<ResultMapping>() {
        {
          add(new ResultMapping.Builder(config, "role", "role", registry.getTypeHandler(String.class))
            .build());
        }
      }).build();
    config.addResultMap(nestedResultMap);
    final MappedStatement ms = new MappedStatement.Builder(config, "selectPerson",
      new StaticSqlSource(config, "select person..."),
      SqlCommandType.SELECT).resultMaps(
        new ArrayList<ResultMap>() {
          {
            add(new ResultMap.Builder(config, "personMap", HashMap.class, new ArrayList<ResultMapping>() {
              {
                add(new ResultMapping.Builder(config, "id", "id", registry.getTypeHandler(Integer.class))
                  .build());
                add(new ResultMapping.Builder(config, "roles").nestedResultMapId("roleMap").build());
              }
            }).build());
          }
        })
        .resultOrdered(true)
        .build();

    final Executor executor = null;
    final ParameterHandler parameterHandler = null;
    final ResultHandler<?> resultHandler = null;
    final BoundSql boundSql = null;
    final RowBounds rowBounds = new RowBounds(5, 1);
    final DefaultResultSetHandler resultSetHandler = new DefaultResultSetHandler(executor, ms, parameterHandler, resultHandler, boundSql, rowBounds);
    when(stmt.getResultSet()).thenReturn(rs);
    when(rsmd.getColumnCount()).thenReturn(2);
    when(rsmd.getColumnLabel(1)).thenReturn("id");
    when(rsmd.getColumnType(1)).thenReturn(Types.INTEGER);
    when(rsmd.getColumnClassName(1)).thenReturn(Integer.class.getCanonicalName());

    final List<Object> results = resultSetHandler.handleResultSets(stmt);
    assertEquals(0, results.size());
  }

  /*
   * Simulate a driver that closes ResultSet automatically when next() returns false (e.g. DB2).
   */
  protected abstract class ImpatientResultSet implements ResultSet {
    private int rowIndex = -1;
    private List<Map<String, Object>> rows = new ArrayList<>();

    protected ImpatientResultSet() {
      Map<String, Object> row = new HashMap<>();
      row.put("id", 1);
      row.put("role", "CEO");
      rows.add(row);
    }

    @Override
    public boolean next() throws SQLException {
      throwIfClosed();
      return ++rowIndex < rows.size();
    }

    @Override
    public boolean isClosed() {
      return rowIndex >= rows.size();
    }

    @Override
    public String getString(String columnLabel) throws SQLException {
      throwIfClosed();
      return (String) rows.get(rowIndex).get(columnLabel);
    }

    @Override
    public int getInt(String columnLabel) throws SQLException {
      throwIfClosed();
      return (Integer) rows.get(rowIndex).get(columnLabel);
    }

    @Override
    public boolean wasNull() throws SQLException {
      throwIfClosed();
      return false;
    }

    @Override
    public ResultSetMetaData getMetaData() {
      return rsmd;
    }

    @Override
    public int getType() throws SQLException {
      throwIfClosed();
      return ResultSet.TYPE_FORWARD_ONLY;
    }

    private void throwIfClosed() throws SQLException {
      if (rowIndex >= rows.size()) {
        throw new SQLException("Invalid operation: result set is closed.");
      }
    }
  }
}
