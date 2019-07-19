
package org.apache.ibatis.builder.xml.dynamic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.scripting.xmltags.ChooseSqlNode;
import org.apache.ibatis.scripting.xmltags.DynamicSqlSource;
import org.apache.ibatis.scripting.xmltags.ForEachSqlNode;
import org.apache.ibatis.scripting.xmltags.IfSqlNode;
import org.apache.ibatis.scripting.xmltags.MixedSqlNode;
import org.apache.ibatis.scripting.xmltags.SetSqlNode;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.scripting.xmltags.TextSqlNode;
import org.apache.ibatis.scripting.xmltags.WhereSqlNode;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DynamicSqlSourceTest extends BaseDataTest {

  final String expected = "SELECT * FROM BLOG";
  TextSqlNode textSqlNode = new TextSqlNode("SELECT * FROM BLOG");
  TextSqlNode textSqlNode2 = new TextSqlNode("WHERE ID = ?");

  /**
   * should Demonstrate Simple Expected Text With No Loops Or Conditionals
   * 应演示没有循环或条件的简单预期文本
   */
  @Test
  void shouldDemonstrateSimpleExpectedTextWithNoLoopsOrConditionals() throws Exception {
    final MixedSqlNode sqlNode = mixedContents(textSqlNode);
    DynamicSqlSource source = createDynamicSqlSource(sqlNode);
    BoundSql boundSql = source.getBoundSql(null);
    System.out.println(boundSql.getSql());
    assertEquals(expected, boundSql.getSql());
  }

  /**
   * should Demonstrate Multipart Expected Text With No Loops Or Conditionals
   * 应演示不带循环或条件的多部分预期文本
   */
  @Test
  void shouldDemonstrateMultipartExpectedTextWithNoLoopsOrConditionals() throws Exception {
    final String expected = "SELECT * FROM BLOG WHERE ID = ?";
    DynamicSqlSource source = createDynamicSqlSource( textSqlNode, textSqlNode2);
    BoundSql boundSql = source.getBoundSql(null);
    System.out.println(boundSql.getSql());
    assertEquals(expected, boundSql.getSql());
  }

  /**
   * should Conditionally Include Where
   * 应有条件地包括
   */
  @Test
  void shouldConditionallyIncludeWhere() throws Exception {
    final String expected = "SELECT * FROM BLOG WHERE ID = ?";
    DynamicSqlSource source = createDynamicSqlSource( textSqlNode, new IfSqlNode(mixedContents(textSqlNode2), "true" ));
    BoundSql boundSql = source.getBoundSql(null);
    System.out.println(boundSql.getSql());
    assertEquals(expected, boundSql.getSql());
  }

  /**
   * should Conditionally Exclude Where
   * 应有条件地排除
   */
  @Test
  void shouldConditionallyExcludeWhere() throws Exception {
    DynamicSqlSource source = createDynamicSqlSource( textSqlNode, new IfSqlNode(mixedContents(textSqlNode2), "false" ));
    BoundSql boundSql = source.getBoundSql(null);
    System.out.println(boundSql.getSql());
    assertEquals(expected, boundSql.getSql());
  }

  @Test
  void shouldConditionallyDefault() throws Exception {
    final String expected = "SELECT * FROM BLOG WHERE CATEGORY = 'DEFAULT'";
    List<SqlNode> sqlNodes = new ArrayList<>() ;
    sqlNodes.add(new IfSqlNode(mixedContents(new TextSqlNode("WHERE CATEGORY = ?")), "false"));
    sqlNodes.add(new IfSqlNode(mixedContents(new TextSqlNode("WHERE CATEGORY = 'NONE'")), "false"));
    DynamicSqlSource source = createDynamicSqlSource( textSqlNode, new ChooseSqlNode(sqlNodes, mixedContents(new TextSqlNode("WHERE CATEGORY = 'DEFAULT'"))));
    BoundSql boundSql = source.getBoundSql(null);
    System.out.println(boundSql.getSql());
    assertEquals(expected, boundSql.getSql());
  }

  @Test
  void shouldConditionallyChooseFirst() throws Exception {
    final String expected = "SELECT * FROM BLOG WHERE CATEGORY = ?";
    DynamicSqlSource source = createDynamicSqlSource(
      textSqlNode,
      new ChooseSqlNode(new ArrayList<SqlNode>() {{
        add(new IfSqlNode(mixedContents(new TextSqlNode("WHERE CATEGORY = ?")), "true"
        ));
        add(new IfSqlNode(mixedContents(new TextSqlNode("WHERE CATEGORY = 'NONE'")), "false"
        ));
      }}, mixedContents(new TextSqlNode("WHERE CATEGORY = 'DEFAULT'"))));
    BoundSql boundSql = source.getBoundSql(null);
    assertEquals(expected, boundSql.getSql());
  }

  @Test
  void shouldConditionallyChooseSecond() throws Exception {
    final String expected = "SELECT * FROM BLOG WHERE CATEGORY = 'NONE'";
    DynamicSqlSource source = createDynamicSqlSource(
      textSqlNode,
      new ChooseSqlNode(new ArrayList<SqlNode>() {{
        add(new IfSqlNode(mixedContents(new TextSqlNode("WHERE CATEGORY = ?")), "false"
        ));
        add(new IfSqlNode(mixedContents(new TextSqlNode("WHERE CATEGORY = 'NONE'")), "true"
        ));
      }}, mixedContents(new TextSqlNode("WHERE CATEGORY = 'DEFAULT'"))));
    BoundSql boundSql = source.getBoundSql(null);
    assertEquals(expected, boundSql.getSql());
  }

  @Test
  void shouldTrimWHEREInsteadOfANDForFirstCondition() throws Exception {
    final String expected = "SELECT * FROM BLOG WHERE  ID = ?";
    DynamicSqlSource source = createDynamicSqlSource(
      textSqlNode,
      new WhereSqlNode(new Configuration(),mixedContents(
        new IfSqlNode(mixedContents(new TextSqlNode("   and ID = ?  ")), "true"
        ),
        new IfSqlNode(mixedContents(new TextSqlNode("   or NAME = ?  ")), "false"
        )
      )));
    BoundSql boundSql = source.getBoundSql(null);
    assertEquals(expected, boundSql.getSql());
  }

  @Test
  void shouldTrimWHEREANDWithLFForFirstCondition() throws Exception {
    final String expected = "SELECT * FROM BLOG WHERE \n ID = ?";
    DynamicSqlSource source = createDynamicSqlSource(
      textSqlNode,
      new WhereSqlNode(new Configuration(),mixedContents(
        new IfSqlNode(mixedContents(new TextSqlNode("   and\n ID = ?  ")), "true" )

      )));
    BoundSql boundSql = source.getBoundSql(null);
    assertEquals(expected, boundSql.getSql());
  }

  @Test
  void shouldTrimWHEREANDWithCRLFForFirstCondition() throws Exception {
    final String expected = "SELECT * FROM BLOG WHERE \r\n ID = ?";
    DynamicSqlSource source = createDynamicSqlSource(
      textSqlNode,
      new WhereSqlNode(new Configuration(),mixedContents(
        new IfSqlNode(mixedContents(new TextSqlNode("   and\r\n ID = ?  ")), "true" )
      )));
    BoundSql boundSql = source.getBoundSql(null);
    assertEquals(expected, boundSql.getSql());
  }

  @Test
  void shouldTrimWHEREANDWithTABForFirstCondition() throws Exception {
    final String expected = "SELECT * FROM BLOG WHERE \t ID = ?";
    DynamicSqlSource source = createDynamicSqlSource(
      textSqlNode,
      new WhereSqlNode(new Configuration(),mixedContents(
        new IfSqlNode(mixedContents(new TextSqlNode("   and\t ID = ?  ")), "true" )
      )));
    BoundSql boundSql = source.getBoundSql(null);
    assertEquals(expected, boundSql.getSql());
  }

  @Test
  void shouldTrimWHEREORWithLFForFirstCondition() throws Exception {
    final String expected = "SELECT * FROM BLOG WHERE \n ID = ?";
    DynamicSqlSource source = createDynamicSqlSource(
      textSqlNode,
      new WhereSqlNode(new Configuration(),mixedContents(
        new IfSqlNode(mixedContents(new TextSqlNode("   or\n ID = ?  ")), "true")
      )));
    BoundSql boundSql = source.getBoundSql(null);
    assertEquals(expected, boundSql.getSql());
  }

  @Test
  void shouldTrimWHEREORWithCRLFForFirstCondition() throws Exception {
    final String expected = "SELECT * FROM BLOG WHERE \r\n ID = ?";
    DynamicSqlSource source = createDynamicSqlSource(
      textSqlNode,
      new WhereSqlNode(new Configuration(),mixedContents(
        new IfSqlNode(mixedContents(new TextSqlNode("   or\r\n ID = ?  ")), "true" )
      )));
    BoundSql boundSql = source.getBoundSql(null);
    assertEquals(expected, boundSql.getSql());
  }

  @Test
  void shouldTrimWHEREORWithTABForFirstCondition() throws Exception {
    final String expected = "SELECT * FROM BLOG WHERE \t ID = ?";
    DynamicSqlSource source = createDynamicSqlSource( textSqlNode,
      new WhereSqlNode(new Configuration(),mixedContents(
        new IfSqlNode(mixedContents(new TextSqlNode("   or\t ID = ?  ")), "true" )
      )));
    BoundSql boundSql = source.getBoundSql(null);
    assertEquals(expected, boundSql.getSql());
  }

  @Test
  void shouldTrimWHEREInsteadOfORForSecondCondition() throws Exception {
    final String expected = "SELECT * FROM BLOG WHERE  NAME = ?";
    DynamicSqlSource source = createDynamicSqlSource(  textSqlNode,
      new WhereSqlNode(new Configuration(),mixedContents(
        new IfSqlNode(mixedContents(new TextSqlNode("   and ID = ?  ")), "false" ),
        new IfSqlNode(mixedContents(new TextSqlNode("   or NAME = ?  ")), "true" )
      )));
    BoundSql boundSql = source.getBoundSql(null);
    assertEquals(expected, boundSql.getSql());
  }

  @Test
  void shouldTrimWHEREInsteadOfANDForBothConditions() throws Exception {
    final String expected = "SELECT * FROM BLOG WHERE  ID = ?   OR NAME = ?";
    DynamicSqlSource source = createDynamicSqlSource(
      textSqlNode,
      new WhereSqlNode(new Configuration(),mixedContents(
        new IfSqlNode(mixedContents(new TextSqlNode("   and ID = ?   ")), "true" ),
        new IfSqlNode(mixedContents(new TextSqlNode("OR NAME = ?  ")), "true" )
      )));
    BoundSql boundSql = source.getBoundSql(null);
    assertEquals(expected, boundSql.getSql());
  }

  @Test
  void shouldTrimNoWhereClause() throws Exception {
    final String expected = "SELECT * FROM BLOG";
    DynamicSqlSource source = createDynamicSqlSource(
      textSqlNode,
      new WhereSqlNode(new Configuration(),mixedContents(
        new IfSqlNode(mixedContents(new TextSqlNode("   and ID = ?   ")), "false"
        ),
        new IfSqlNode(mixedContents(new TextSqlNode("OR NAME = ?  ")), "false"
        )
      )));
    BoundSql boundSql = source.getBoundSql(null);
    assertEquals(expected, boundSql.getSql());
  }

  @Test
  void shouldTrimSETInsteadOfCOMMAForBothConditions() throws Exception {
    final String expected = "UPDATE BLOG SET ID = ?,  NAME = ?";
    DynamicSqlSource source = createDynamicSqlSource(
      new TextSqlNode("UPDATE BLOG"),
      new SetSqlNode(new Configuration(),mixedContents(
        new IfSqlNode(mixedContents(new TextSqlNode(" ID = ?, ")), "true"
        ),
        new IfSqlNode(mixedContents(new TextSqlNode(" NAME = ?, ")), "true"
        )
      )));
    BoundSql boundSql = source.getBoundSql(null);
    assertEquals(expected, boundSql.getSql());
  }

  @Test
  void shouldTrimCommaAfterSET() throws Exception {
    final String expected = "UPDATE BLOG SET  NAME = ?";
    DynamicSqlSource source = createDynamicSqlSource(
      new TextSqlNode("UPDATE BLOG"),
      new SetSqlNode(new Configuration(), mixedContents(
        new IfSqlNode(mixedContents(new TextSqlNode("ID = ?")), "false"),
        new IfSqlNode(mixedContents(new TextSqlNode(", NAME = ?")), "true"))));
    BoundSql boundSql = source.getBoundSql(null);
    assertEquals(expected, boundSql.getSql());
  }

  @Test
  void shouldTrimNoSetClause() throws Exception {
    final String expected = "UPDATE BLOG";
    DynamicSqlSource source = createDynamicSqlSource(
      new TextSqlNode("UPDATE BLOG"),
      new SetSqlNode(new Configuration(),mixedContents(
        new IfSqlNode(mixedContents(new TextSqlNode("   , ID = ?   ")), "false"
        ),
        new IfSqlNode(mixedContents(new TextSqlNode(", NAME = ?  ")), "false"
        )
      )));
    BoundSql boundSql = source.getBoundSql(null);
    assertEquals(expected, boundSql.getSql());
  }

  @Test
  void shouldIterateOnceForEachItemInCollection() throws Exception {
    final HashMap<String, String[]> parameterObject = new HashMap<String, String[]>() {{
      put("array", new String[]{"one", "two", "three"});
    }};
    final String expected = "SELECT * FROM BLOG WHERE ID in (  one = ? AND two = ? AND three = ? )";
    DynamicSqlSource source = createDynamicSqlSource(
      new TextSqlNode("SELECT * FROM BLOG WHERE ID in"),
      new ForEachSqlNode(new Configuration(),mixedContents(new TextSqlNode("${item} = #{item}")), "array", "index", "item", "(", ")", "AND"));
    BoundSql boundSql = source.getBoundSql(parameterObject);
    assertEquals(expected, boundSql.getSql());
    assertEquals(3, boundSql.getParameterMappings().size());
    assertEquals("__frch_item_0", boundSql.getParameterMappings().get(0).getProperty());
    assertEquals("__frch_item_1", boundSql.getParameterMappings().get(1).getProperty());
    assertEquals("__frch_item_2", boundSql.getParameterMappings().get(2).getProperty());
  }

  @Test
  void shouldHandleOgnlExpression() throws Exception {
    final HashMap<String, String> parameterObject = new HashMap<String, String>() {{
      put("name", "Steve");
    }};
    final String expected = "Expression test: 3 / yes.";
    DynamicSqlSource source = createDynamicSqlSource(new TextSqlNode("Expression test: ${name.indexOf('v')} / ${name in {'Bob', 'Steve'\\} ? 'yes' : 'no'}."));
    BoundSql boundSql = source.getBoundSql(parameterObject);
    assertEquals(expected, boundSql.getSql());
  }

  @Test
  void shouldSkipForEachWhenCollectionIsEmpty() throws Exception {
    final HashMap<String, Integer[]> parameterObject = new HashMap<String, Integer[]>() {{
      put("array", new Integer[] {});
    }};
    final String expected = "SELECT * FROM BLOG";
    DynamicSqlSource source = createDynamicSqlSource(textSqlNode,
      new ForEachSqlNode(new Configuration(), mixedContents(
        new TextSqlNode("#{item}")), "array", null, "item", "WHERE id in (", ")", ","));
    BoundSql boundSql = source.getBoundSql(parameterObject);
    assertEquals(expected, boundSql.getSql());
    assertEquals(0, boundSql.getParameterMappings().size());
  }

  @Test
  void shouldPerformStrictMatchOnForEachVariableSubstitution() throws Exception {
    final Map<String, Object> param = new HashMap<>();
    final Map<String, String> uuu = new HashMap<>();
    uuu.put("u", "xyz");
    List<Bean> uuuu = new ArrayList<>();
    uuuu.add(new Bean("bean id"));
    param.put("uuu", uuu);
    param.put("uuuu", uuuu);
    DynamicSqlSource source = createDynamicSqlSource(
      new TextSqlNode("INSERT INTO BLOG (ID, NAME, NOTE, COMMENT) VALUES"),
      new ForEachSqlNode(new Configuration(),mixedContents(
        new TextSqlNode("#{uuu.u}, #{u.id}, #{ u,typeHandler=org.apache.ibatis.type.StringTypeHandler},"
          + " #{u:VARCHAR,typeHandler=org.apache.ibatis.type.StringTypeHandler}")), "uuuu", "uu", "u", "(", ")", ","));
    BoundSql boundSql = source.getBoundSql(param);
    assertEquals(4, boundSql.getParameterMappings().size());
    assertEquals("uuu.u", boundSql.getParameterMappings().get(0).getProperty());
    assertEquals("__frch_u_0.id", boundSql.getParameterMappings().get(1).getProperty());
    assertEquals("__frch_u_0", boundSql.getParameterMappings().get(2).getProperty());
    assertEquals("__frch_u_0", boundSql.getParameterMappings().get(3).getProperty());
  }

  private DynamicSqlSource createDynamicSqlSource(SqlNode... contents) throws IOException, SQLException {
    createBlogDataSource();
    final String resource = "org/apache/ibatis/builder/MapperConfig.xml";
    final Reader reader = Resources.getResourceAsReader(resource);
    SqlSessionFactory sqlMapper = new SqlSessionFactoryBuilder().build(reader);
    Configuration configuration = sqlMapper.getConfiguration();
    MixedSqlNode sqlNode = mixedContents(contents);
    return new DynamicSqlSource(configuration, sqlNode);
  }

  private MixedSqlNode mixedContents(SqlNode... contents) {
    return new MixedSqlNode(Arrays.asList(contents));
  }

  @Test
  void shouldMapNullStringsToEmptyStrings() {
    final MixedSqlNode sqlNode = mixedContents(new TextSqlNode("id=${id}"));
    final DynamicSqlSource source = new DynamicSqlSource(new Configuration(), sqlNode);
    String sql = source.getBoundSql(new Bean(null)).getSql();
    Assertions.assertEquals("id=", sql);
  }

  public static class Bean {
    public String id;
    Bean(String property) {
      this.id = property;
    }
    public String getId() {
      return id;
    }
    public void setId(String property) {
      this.id = property;
    }
  }

}
