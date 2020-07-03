
package org.apache.ibatis.submitted.lazy_deserialize;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @since 2011-04-06T10:58:55+0200
 */
class LazyDeserializeTest {

  private static final int FOO_ID = 1;
  private static final int BAR_ID = 10;
  private static SqlSessionFactory factory;

  public static Configuration getConfiguration() {
    return factory.getConfiguration();
  }

  @BeforeEach
  void setupClass() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/lazy_deserialize/ibatisConfig.xml")) {
      factory = new SqlSessionFactoryBuilder().build(reader);
    }
    BaseDataTest.runScript(factory.getConfiguration().getEnvironment().getDataSource(),"org/apache/ibatis/submitted/lazy_deserialize/CreateDB.sql");
  }

  @Test
  void testLoadLazyDeserialize() throws Exception {
    factory.getConfiguration().setConfigurationFactory(this.getClass());
    try (SqlSession session = factory.openSession()) {
      final Mapper mapper = session.getMapper(Mapper.class);
      final LazyObjectFoo foo = mapper.loadFoo(FOO_ID);

      final byte[] serializedFoo = this.serializeFoo(foo);
      final LazyObjectFoo deserializedFoo = this.deserializeFoo(serializedFoo);

      assertNotNull(deserializedFoo);
      assertEquals(Integer.valueOf(FOO_ID), deserializedFoo.getId());
      assertNotNull(deserializedFoo.getLazyObjectBar());
      assertEquals(Integer.valueOf(BAR_ID), deserializedFoo.getLazyObjectBar().getId());
    }
  }

  @Test
  void testLoadLazyDeserializeWithoutConfigurationFactory() throws Exception {
    try (SqlSession session = factory.openSession()) {
      final Mapper mapper = session.getMapper(Mapper.class);
      final LazyObjectFoo foo = mapper.loadFoo(FOO_ID);
      final byte[] serializedFoo = this.serializeFoo(foo);
      final LazyObjectFoo deserializedFoo = this.deserializeFoo(serializedFoo);
      try {
        deserializedFoo.getLazyObjectBar();
        fail();
      } catch (ExecutorException e) {
        assertTrue(e.getMessage().contains("Cannot get Configuration as configuration factory was not set."));
      }
    }
  }

  private byte[] serializeFoo(final LazyObjectFoo foo) throws Exception {
    try (ByteArrayOutputStream bos = new ByteArrayOutputStream();ObjectOutputStream oos = new ObjectOutputStream(bos)) {
      oos.writeObject(foo);
      return bos.toByteArray();
    }
  }

  private LazyObjectFoo deserializeFoo(final byte[] serializedFoo) throws Exception {
    try (ByteArrayInputStream bis = new ByteArrayInputStream(serializedFoo); ObjectInputStream ios = new ObjectInputStream(bis)) {
      return (LazyObjectFoo) ios.readObject();
    }
  }

}
