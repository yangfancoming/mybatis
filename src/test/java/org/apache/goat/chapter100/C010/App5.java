package org.apache.goat.chapter100.C010;

import org.apache.goat.MyBaseDataTest;
import org.apache.goat.common.Employee;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;


class App5 extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/C010/mybatis-config.xml";
  public static final String DBSQL = "org/apache/goat/common/CreateDB2.sql";

  /**
   * Map传参
   *
   * 如果多个参数不是业务模型中的数据，没有对应的pojo，不经常使用，
   * 为了方便，我们也可以传入map
   *    #{key}：取出map中对应的值
   */
  @Test
  void getEmpById() throws Exception  {
    setUpByReader(XMLPATH);
    EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
    Map<String, Object> map = new HashMap<>();
    map.put("id", 2);
    map.put("lastName", "jane");
    Employee temp = mapper.getEmpByIdAndLastName5(map);
    System.out.println(temp);
  }

}
