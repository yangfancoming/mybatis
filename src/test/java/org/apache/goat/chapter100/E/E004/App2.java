package org.apache.goat.chapter100.E.E004;

import org.apache.common.MyBaseDataTest;
import org.apache.goat.common.model.Employee;
import org.apache.ibatis.type.TypeAliasRegistry;
import org.junit.jupiter.api.Test;

import java.util.Map;


class App2 extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/E/E004/mybatis-config.xml";

  /**
   * resultType：如果返回的是一个Map，  key就是列名，值就是对应的值
   * {@link TypeAliasRegistry}
  */


  @Test
  void getEmpByIdReturnMap() throws Exception  {
    setUpByReader(XMLPATH);
    EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
    Map<String, Object> temp = mapper.getEmpByIdReturnMap(1);
    System.out.println(temp);
  }

  /**
   * r//多条记录封装一个map：Map<Integer,Employee>:key是这条记录的主键，value是记录封装后的javaBean
   * {@link TypeAliasRegistry}
   */
  @Test
  void getEmpById() throws Exception  {
    setUpByReader(XMLPATH);
    EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
    Map<String, Employee> temp = mapper.getEmpByLastNameLikeReturnMap("%t%");
    System.out.println(temp);
  }
}
