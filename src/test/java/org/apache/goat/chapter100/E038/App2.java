package org.apache.goat.chapter100.E038;

import org.apache.goat.MyBaseDataTest;
import org.apache.goat.common.Department;
import org.apache.goat.common.Employee2;
import org.junit.jupiter.api.Test;

import java.util.List;


class App2 extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/E038/mybatis-config.xml";

  /**
   * 第一步查询
   */
  @Test
  void test1() throws Exception  {
    setUpByReader(XMLPATH);
    EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
    List<Employee2> list = mapper.getEmpsByDeptId(2,"goat");
    System.out.println(list);
  }


  /**
   * 第二步查询
   * collection 分步查询&延迟加载
   * Preparing: select * from tbl_employee where d_id = ? and last_name = ?
   * Parameters: 1(Integer), 开发部(String)
   *  Total: 0
   *
   *  即 通过 column="{deptId=id,lastName=dept_name}" 的配置 将两个参数都传递过来了
   */
  @Test
  void test2() throws Exception  {
    setUpByReader(XMLPATH);
    DepartmentMapper mapper = sqlSession.getMapper(DepartmentMapper.class);
    Department employee = mapper.getDeptByIdStep(1);
    System.out.println(employee); // 急切加载
  }

  /**
   *  <collection> 标签的 fetchType="eager" 属性 会覆盖全局xml 中的  两个属性
   *     <setting name="lazyLoadingEnabled" value="true"/>
   *     <setting name="aggressiveLazyLoading" value="false"/>
   *  即  fetchType="eager"  时  即使全局 设置懒加载 也不好使，会以fetchType为准 进行急切加载
  */
  @Test
  void test3() throws Exception  {
    setUpByReader(XMLPATH);
    DepartmentMapper mapper = sqlSession.getMapper(DepartmentMapper.class);
    Department employee = mapper.getDeptByIdStep(1);
    System.out.println(employee.getDepartmentName()); // 懒加载
  }


}
