package org.apache.goat.chapter100.E.E010;


import org.apache.common.MyBaseDataTest;
import org.apache.goat.common.model.Employee;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.junit.jupiter.api.Test;


class App2 extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/E/E010/mybatis-config.xml";

  /**
   * 正常查询 可以看到 lastName='null'  因为 Employee实体类的lastName属性  与 数据库表中的 last_name字段  不对应，
   * 执行结果为： Employee{id=1, lastName='null', email='tom@qq.com', gender='0'}
   * 但是加上：EmployeeMapper.xml 局部配置文件中的 <result property="lastName" column="last_name" />  后执行结果为：
   * Employee{id=1, lastName='tom', email='tom@qq.com', gender='0'}
   * @see XMLMapperBuilder#resultMapElements(java.util.List)
   */
  @Test
  void getEmpById() throws Exception  {
    setUpByReader(XMLPATH);
    EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
    Employee employee = mapper.getEmpById2(1);
    System.out.println(employee);
  }

}
