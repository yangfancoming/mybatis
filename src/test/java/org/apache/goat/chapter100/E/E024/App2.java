package org.apache.goat.chapter100.E.E024;

import org.apache.common.MyBaseDataTest;
import org.apache.goat.common.model.Employee2;
import org.junit.jupiter.api.Test;


class App2 extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/E/E024/mybatis-config.xml";

  /**
   *  association 分步查询&延迟加载
   *  运行结果可以看到  只执行了一次 sql
   */
  @Test
  void getEmpByIdStep() throws Exception  {
    setUpByReader(XMLPATH);
    EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
    Employee2 employee = mapper.getEmpByIdStep(1);
    // 注意： 这里千万要注意 不能使用到 dept 否则 无法测试 延时加载
    System.out.println(employee.getId());
  }

  /**
   *  运行结果可以看到  执行两个sql    再使用到 dept 时候 才去查询的
   */
  @Test
  void test2() throws Exception  {
    setUpByReader(XMLPATH);
    EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
    Employee2 employee = mapper.getEmpByIdStep(1);
    // 涉及到  dept 对象了  会查询2次  不能延时加载了
    System.out.println(employee);
  }


}
