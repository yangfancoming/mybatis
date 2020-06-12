package org.apache.goat.chapter100.A.A038;

import org.apache.common.MyBaseDataTest;
import org.apache.goat.common.model.Foo;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.builder.xml.XMLStatementBuilder;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Map;


class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/A/A038/mybatis-config.xml";
  public static final String DBSQL = "org/apache/goat/common/CreateDB.sql";
  public static final String NAME_SPACE = "com.goat.test.namespace.A038.selectById";

  @BeforeAll
  static void setUp() throws Exception {
    setUpByReader(XMLPATH,DBSQL);
  }

  /**
   * 使用  <environments default="pro_mysql"> 标签 会执行
   * <select id="selectById" parameterType="int" resultType="org.apache.goat.common.Foo"  databaseId="mysql">
   *   结果：  id=1, lastname=11, firstname=1
   *
   * 使用  <environments default="dev_hsqldb"> 标签 会执行
   * <select id="selectById" parameterType="int" resultType="org.apache.goat.common.Foo"  databaseId="hsqldb">
   *   结果：  id=1, lastname=null, firstname=111
   * 
   * 局部xml中的两个sql 标签是一样的  mybatis 根据 databaseId 属性不同 进行区分
  */
  @Test
  void test1() {
    Foo foo = sqlSession.selectOne(NAME_SPACE,1);
    Assert.assertEquals(1,foo.getId());
  }

  Configuration configuration = sqlSessionFactory.getConfiguration();
  MapperBuilderAssistant assistant = new MapperBuilderAssistant(configuration,"");
  XMLStatementBuilder xmlStatementBuilder = new XMLStatementBuilder(configuration,assistant,null);

  // 改成public后 直接访问测试
  @Test
  void databaseIdMatchesCurrentTest1() {
    boolean mark = xmlStatementBuilder.databaseIdMatchesCurrent("", "", "");
    System.out.println(mark);
  }

  // 通过反射 测试 private 方法  databaseIdMatchesCurrent()
  @Test
  void databaseIdMatchesCurrentTest2() throws Exception{
    Method testNoParamMethod = xmlStatementBuilder.getClass().getDeclaredMethod(NAME_SPACE, String.class,String.class,String.class);
    testNoParamMethod.setAccessible(true);
    //通过反射调用 databaseIdMatchesCurrent() 方法
    Object result = testNoParamMethod.invoke(xmlStatementBuilder, "","","");
    System.out.println(result);
  }

  /**
   * 测试 MappedStatement 会通过 StrictMap 将一个<CRUD> 标签同时put 2个到mappedStatements中
   * com.goat.test.namespace.A038.selectById -> {MappedStatement@3170}
   * selectById -> {MappedStatement@3170}
  */
  @Test
  void test() {
    Map<String, MappedStatement> mappedStatement = configuration.getMappedStatement();
    Assert.assertEquals("hsqldb",mappedStatement.get(Configuration.StrictMap.getShortName(NAME_SPACE)).getDatabaseId());
    Assert.assertEquals("hsqldb",mappedStatement.get(NAME_SPACE).getDatabaseId());

    // 从 MappedStatement 中获取sql文本
//    mappedStatement.get(NAME_SPACE).getBoundSql()
  }
}
