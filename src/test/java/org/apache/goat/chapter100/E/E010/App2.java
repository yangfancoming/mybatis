package org.apache.goat.chapter100.E.E010;


import org.apache.common.MyBaseDataTest;
import org.apache.goat.common.model.Employee;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class App2 extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/E/E010/mybatis-config.xml";

  EmployeeMapper mapper;

  @BeforeEach
  public void before() throws Exception{
    setUpByReader(XMLPATH);
    mapper = sqlSession.getMapper(EmployeeMapper.class);
  }

  /**
   * 正常查询 可以看到 lastName='null'  因为 Employee实体类的lastName属性  与 数据库表中的 last_name字段  不对应，
   * 执行结果为： Employee{id=1, lastName='null', email='tom@qq.com', gender='0'}
   * @see XMLMapperBuilder#resultMapElements(java.util.List)
   */
  @Test
  public void testWithNoResultMap() {
    Employee temp = mapper.testWithNoResultMap(1);
    Assert.assertEquals(null,temp.getLastName());
  }

  /**
   *  加入 <resultMap> 标签  和 resultMap="MySimpleEmp"  后的查询  就可以查询出结果了
   * Employee{id=1, lastName='tom', email='tom@qq.comMysql', gender='0'}
   */
  @Test
  public void testWithResultMap() {
    Employee employee = mapper.testWithResultMap(1);
    Assert.assertEquals("tom",employee.getLastName());
  }
}
