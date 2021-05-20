
package org.apache.ibatis.executor.loader;

import net.sf.cglib.proxy.Factory;
import org.apache.ibatis.domain.blog.Author;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.executor.loader.cglib.CglibProxyFactory;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class CglibProxyTest extends SerializableProxyTest {

  @BeforeAll
  static void createProxyFactory() {
    proxyFactory = new CglibProxyFactory();
  }

  @Test
  void shouldCreateAProxyForAPartiallyLoadedBean() throws Exception {
    ResultLoaderMap loader = new ResultLoaderMap();
    loader.addLoader("id", null, null);
    Object proxy = proxyFactory.createProxy(author, loader, new Configuration(), new DefaultObjectFactory(), new ArrayList<>(), new ArrayList<>());
    Author author2 = (Author) deserialize(serialize((Serializable) proxy));
    assertTrue(author2 instanceof Factory);
  }

  @Test
  void shouldFailCallingAnUnloadedProperty() {
    // yes, it must go in uppercase
    HashMap<String, ResultLoaderMap.LoadPair> unloadedProperties = new HashMap<>();
    unloadedProperties.put("ID", null);
    Author author2 = (Author) ((CglibProxyFactory)proxyFactory).createDeserializationProxy(author, unloadedProperties, new DefaultObjectFactory(), new ArrayList<>(), new ArrayList<>());
    Assertions.assertThrows(ExecutorException.class, author2::getId);
  }

  @Test
  void shouldLetCallALoadedProperty() {
    Author author2 = (Author) ((CglibProxyFactory)proxyFactory).createDeserializationProxy(author, new HashMap<>(), new DefaultObjectFactory(), new ArrayList<>(), new ArrayList<>());
    assertEquals(999, author2.getId());
  }

  @Test
  void shouldSerizalizeADeserlizaliedProxy() throws Exception {
    Object proxy = ((CglibProxyFactory)proxyFactory).createDeserializationProxy(author, new HashMap<>(), new DefaultObjectFactory(), new ArrayList<>(), new ArrayList<>());
    Author author2 = (Author) deserialize(serialize((Serializable) proxy));
    assertEquals(author, author2);
    assertNotEquals(author.getClass(), author2.getClass());
  }

}
