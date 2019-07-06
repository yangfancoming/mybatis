
package org.apache.ibatis.submitted.association_nested;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

class FolderMapperTest {

  /**
   * Root/
   *    Folder 1/
   *    Folder 2/
   *      Folder 2_1
   *      Folder 2_2
   */

  @Test
  void testFindWithChildren() throws Exception {
    try (Connection conn = DriverManager.getConnection("jdbc:hsqldb:mem:association_nested", "SA", "");
         Statement stmt = conn.createStatement()) {
      stmt.execute("create table folder (id int, name varchar(100), parent_id int)");
      stmt.execute("insert into folder (id, name) values(1, 'Root')");
      stmt.execute("insert into folder values(2, 'Folder 1', 1)");
      stmt.execute("insert into folder values(3, 'Folder 2', 1)");
      stmt.execute("insert into folder values(4, 'Folder 2_1', 3)");
      stmt.execute("insert into folder values(5, 'Folder 2_2', 3)");
    }

    String resource = "org/apache/ibatis/submitted/association_nested/mybatis-config.xml";
    // MyBatis 提供的工具类 Resources 加载配置文件，得到一个输入流
    try (InputStream inputStream = Resources.getResourceAsStream(resource)) {
      //然后再通过 SqlSessionFactoryBuilder 对象的 build 方法 根据配置文件构建 SqlSessionFactory 对象
      SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
      try (SqlSession session = sqlSessionFactory.openSession()) {
        FolderMapper postMapper = session.getMapper(FolderMapper.class);
        List<FolderFlatTree> folders = postMapper.findWithSubFolders("Root");
        Assertions.assertEquals(3, folders.size());
      }
    }
  }

}
