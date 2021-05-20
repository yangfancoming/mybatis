
package org.apache.ibatis.executor.resultset;

import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultResultSetHandlerTest {

  @Mock
  private Statement stmt;
  @Mock
  private ResultSet rs;
  @Mock
  private ResultSetMetaData rsmd;
  @Mock
  private Connection conn;
  @Mock
  private DatabaseMetaData dbmd;

  /**
   * Contrary to the spec, some drivers require case-sensitive column names when getting result.
   * @see <a href="http://code.google.com/p/mybatis/issues/detail?id=557">Issue 557</a>
   */
  @Test
  void shouldRetainColumnNameCase() throws Exception {
    final MappedStatement ms = getMappedStatement();
    final Executor executor = null;
    final ParameterHandler parameterHandler = null;
    final ResultHandler resultHandler = null;
    final BoundSql boundSql = null;
    final RowBounds rowBounds = new RowBounds(0, 100);
    final DefaultResultSetHandler fastResultSetHandler = new DefaultResultSetHandler(executor, ms, parameterHandler, resultHandler, boundSql, rowBounds);

    when(stmt.getResultSet()).thenReturn(rs);
    when(rs.getMetaData()).thenReturn(rsmd);
    when(rs.getType()).thenReturn(ResultSet.TYPE_FORWARD_ONLY);
    when(rs.next()).thenReturn(true).thenReturn(false);
    when(rs.getInt("CoLuMn1")).thenReturn(100);
    when(rsmd.getColumnCount()).thenReturn(1);
    when(rsmd.getColumnLabel(1)).thenReturn("CoLuMn1");
    when(rsmd.getColumnType(1)).thenReturn(Types.INTEGER);
    when(rsmd.getColumnClassName(1)).thenReturn(Integer.class.getCanonicalName());
    when(stmt.getConnection()).thenReturn(conn);
    when(conn.getMetaData()).thenReturn(dbmd);
    when(dbmd.supportsMultipleResultSets()).thenReturn(false); // for simplicity.

    final List<Object> results = fastResultSetHandler.handleResultSets(stmt);
    assertEquals(1, results.size());
    assertEquals(100, ((HashMap) results.get(0)).get("cOlUmN1"));
  }

  @Test
  void shouldThrowExceptionWithColumnName() throws Exception {
    final MappedStatement ms = getMappedStatement();
    final RowBounds rowBounds = new RowBounds(0, 100);
    final DefaultResultSetHandler defaultResultSetHandler = new DefaultResultSetHandler(null, ms,null, null, null, rowBounds);
    final ResultSetWrapper rsw = mock(ResultSetWrapper.class);
    when(rsw.getResultSet()).thenReturn(mock(ResultSet.class));
    final ResultMapping resultMapping = mock(ResultMapping.class);
    final TypeHandler typeHandler = mock(TypeHandler.class);
    when(resultMapping.getColumn()).thenReturn("column");
    when(resultMapping.getTypeHandler()).thenReturn(typeHandler);
    when(typeHandler.getResult(any(ResultSet.class), any(String.class))).thenThrow(new SQLException("exception"));
    List<ResultMapping> constructorMappings = Collections.singletonList(resultMapping);
    try {
      defaultResultSetHandler.createParameterizedResultObject(rsw, null/*resultType*/, constructorMappings,null, null, null);
      Assertions.fail("Should have thrown ExecutorException");
    } catch (Exception e) {
      Assertions.assertTrue(e instanceof ExecutorException, "Expected ExecutorException");
      Assertions.assertTrue(e.getMessage().contains("mapping: " + resultMapping.toString()));
    }
  }

  MappedStatement getMappedStatement() {
    final Configuration config = new Configuration();
    final TypeHandlerRegistry registry = config.getTypeHandlerRegistry();
    return new MappedStatement.Builder(config, "testSelect", new StaticSqlSource(config, "some select statement"), SqlCommandType.SELECT).resultMaps(
        new ArrayList<ResultMap>() {
          {
            add(new ResultMap.Builder(config, "testMap", HashMap.class, new ArrayList<ResultMapping>() {
              {
                add(new ResultMapping.Builder(config, "cOlUmN1", "CoLuMn1", registry.getTypeHandler(Integer.class)).build());
              }
            }).build());
          }
        }).build();
  }
}
