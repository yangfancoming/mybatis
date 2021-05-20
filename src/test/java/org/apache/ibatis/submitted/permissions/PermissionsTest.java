
package org.apache.ibatis.submitted.permissions;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.util.List;

class PermissionsTest {

  private static SqlSessionFactory sqlSessionFactory;

  @BeforeAll
  static void setUp() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/permissions/mybatis-config.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    }
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),"org/apache/ibatis/submitted/permissions/CreateDB.sql");
  }

  @Test // see issue #168
  void checkNestedResultMapLoop() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      final PermissionsMapper mapper = sqlSession.getMapper(PermissionsMapper.class);

      final List<Resource> resources = mapper.getResources();
      Assertions.assertEquals(2, resources.size());

      final Resource firstResource = resources.get(0);
      final List<Principal> principalPermissions = firstResource.getPrincipals();
      Assertions.assertEquals(1, principalPermissions.size());

      final Principal firstPrincipal = principalPermissions.get(0);
      final List<Permission> permissions = firstPrincipal.getPermissions();
      Assertions.assertEquals(2, permissions.size());

      final Permission firstPermission = firstPrincipal.getPermissions().get(0);
      Assertions.assertSame(firstResource, firstPermission.getResource());
      final Permission secondPermission = firstPrincipal.getPermissions().get(1);
      Assertions.assertSame(firstResource, secondPermission.getResource());
    }
  }

  @Test
  void checkNestedSelectLoop() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      final PermissionsMapper mapper = sqlSession.getMapper(PermissionsMapper.class);

      final List<Resource> resources = mapper.getResource("read");
      Assertions.assertEquals(1, resources.size());

      final Resource firstResource = resources.get(0);
      final List<Principal> principalPermissions = firstResource.getPrincipals();
      Assertions.assertEquals(1, principalPermissions.size());

      final Principal firstPrincipal = principalPermissions.get(0);
      final List<Permission> permissions = firstPrincipal.getPermissions();
      Assertions.assertEquals(4, permissions.size());

      boolean readFound = false;
      for (Permission permission : permissions) {
        if ("read".equals(permission.getPermission())) {
          Assertions.assertSame(firstResource, permission.getResource());
          readFound = true;
        }
      }

      if (!readFound) {
        Assertions.fail();
      }
    }
  }

}
