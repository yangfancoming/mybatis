package org.apache.goat.chapter100.E.E056;

import org.apache.common.MyBaseDataTest;
import org.apache.goat.common.model.Customer;
import org.apache.ibatis.builder.xml.XMLIncludeTransformer;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.builder.xml.XMLStatementBuilder;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;


class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/E/E056/mybatis-config.xml";

  CustomerMapper mapper;

  @BeforeEach
  public void test() throws Exception {
    setUpByReader(XMLPATH);
    mapper = sqlSession.getMapper(CustomerMapper.class);
  }

  @Test
  void test1() {
    List<Customer> list = mapper.getTest( new Customer(2));
    System.out.println(list);
  }

  @Test
  void test2() {
    List<Customer> list = mapper.getTest2(new Customer(2));
    System.out.println(list);
  }

  @Test
  void test3() {
    Customer test3 = mapper.getTest3(1);
    System.out.println(test3);
  }

  @Test
  void temp()  {
    Configuration configuration = sqlSessionFactory.getConfiguration();
    Map<String, XNode> sqlFragments = configuration.getSqlFragments();
    System.out.println(sqlFragments);
  }


  /**
   * 1.sqlFragment的解析过程
   * {@link XMLMapperBuilder#configurationElement(org.apache.ibatis.parsing.XNode)}
   * {@link XMLMapperBuilder#sqlElement(java.util.List)}
   *
   * 2.select|insert|update|delete标签中，解析include标签的过程
   * {@link XMLMapperBuilder#configurationElement(org.apache.ibatis.parsing.XNode)}
   * {@link org.apache.ibatis.builder.xml.XMLMapperBuilder#buildStatementFromContext(java.util.List)}
   * {@link XMLStatementBuilder#parseStatementNode()}
   * {@link XMLIncludeTransformer#applyIncludes(org.w3c.dom.Node)}
   * {@link XMLIncludeTransformer#applyIncludes(org.w3c.dom.Node, java.util.Properties, boolean)}
   */
  @Test
  void test4() {
    int count = mapper.getTest4();
    System.out.println(count);
  }
  /**
   *
   * ①解析节点
   *
   *    <select id="countAll" resultType="int">
   *    select count(1) from (
   *    <include refid="studentProperties"></include>
   *    ) tmp
   *    </select>

   * ②include节点替换为sqlFragment节点
   * <select id="countAll" resultType="int">
   * 		select count(1) from (
   * 				<sql id="studentProperties">
   * 					select stud_id as studId, name, email, dob	, phone from students
   * 				</sql>
   * 		) tmp
   * 	</select>
   *
   * 	③将sqlFragment的子节点（文本节点）insert到sqlFragment节点的前面。注意，对于dom来说，文本也是一个节点，叫TextNode。
   * 	<select id="countAll" resultType="int">
   * 		select count(1) from (
   * 	select stud_id as studId, name, email, dob	, phone from students
   * 				<sql id="studentProperties">
   * 				   select stud_id as studId, name, email, dob	, phone from students
   * 				</sql>
   * 		) tmp
   * 	</select>
   *
   *
   * 	④移除sqlFragment节点
   * 	<select id="countAll" resultType="int">
   * 		select count(1) from (
   * 	select stud_id as studId, name, email, dob	, phone from students
   * 		) tmp
   * 	</select>
  */

  @Test
  void test5() {
    Customer temp = mapper.selectById(1);
    System.out.println(temp);
  }
}
