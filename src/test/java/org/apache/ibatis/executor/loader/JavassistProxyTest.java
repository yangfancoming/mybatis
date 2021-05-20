
package org.apache.ibatis.executor.loader;

import javassist.util.proxy.Proxy;
import org.apache.ibatis.domain.blog.Author;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.executor.loader.javassist.JavassistProxyFactory;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class JavassistProxyTest extends SerializableProxyTest {

  @BeforeAll
  static void createProxyFactory() {
    proxyFactory = new JavassistProxyFactory();
  }

  @Test
  void shouldCreateAProxyForAPartiallyLoadedBean() throws Exception {
    ResultLoaderMap loader = new ResultLoaderMap();
    loader.addLoader("id", null, null);
    Object proxy = proxyFactory.createProxy(author, loader, new Configuration(), new DefaultObjectFactory(), new ArrayList<>(), new ArrayList<>());
    Author author2 = (Author) deserialize(serialize((Serializable) proxy));
    assertTrue(author2 instanceof Proxy);
  }

  @Test
  void shouldFailCallingAnUnloadedProperty() {
    // yes, it must go in uppercase
    HashMap<String, ResultLoaderMap.LoadPair> unloadedProperties = new HashMap<> ();
    unloadedProperties.put("ID", null);
    Author author2 = (Author) ((JavassistProxyFactory)proxyFactory).createDeserializationProxy(author, unloadedProperties, new DefaultObjectFactory(), new ArrayList<>(), new ArrayList<>());
    Assertions.assertThrows(ExecutorException.class, author2::getId);
  }

  @Test
  void shouldLetCallALoadedProperty() {
    Author author2 = (Author) ((JavassistProxyFactory)proxyFactory).createDeserializationProxy(author, new HashMap<>(), new DefaultObjectFactory(), new ArrayList<>(), new ArrayList<>());
    assertEquals(999, author2.getId());
  }

  @Test
  void shouldSerizalizeADeserlizaliedProxy() throws Exception {
    Object proxy = ((JavassistProxyFactory)proxyFactory).createDeserializationProxy(author, new HashMap<> (), new DefaultObjectFactory(), new ArrayList<>(), new ArrayList<>());
    Author author2 = (Author) deserialize(serialize((Serializable) proxy));
    assertEquals(author, author2);
    assertNotEquals(author.getClass(), author2.getClass());
  }

}
