package org.apache.goat.chapter800;

import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.reflection.wrapper.BeanWrapper;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 64274 on 2019/11/5.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/11/5---10:08
 */
public class App {

  ObjectFactory objectFactory =  new DefaultObjectFactory();
  ObjectWrapperFactory objectWrapperFactory =  new DefaultObjectWrapperFactory();
  ReflectorFactory reflectorFactory =  new DefaultReflectorFactory();

  @Test
  public void testBean(){
    Animal animal = new Animal();
    MetaObject metaObject = MetaObject.forObject(animal, objectFactory, objectWrapperFactory, reflectorFactory);
    metaObject.setValue("name","bean");
    System.out.println(animal.getName()); // bean
  }

  @Test
  public void testCollection(){
    Animal animal = new Animal();
    animal.setName("collection");
    List<Animal> list = new ArrayList<>();
    MetaObject metaObject = MetaObject.forObject(list, objectFactory, objectWrapperFactory, reflectorFactory);
    metaObject.add(animal);
    System.out.println(list.get(0).getName()); // collection
  }

  @Test
  public void testMap(){
    Animal animal = new Animal();
    animal.setName("map");
    Map<String,Animal> map = new HashMap<>();
    MetaObject metaObject = MetaObject.forObject(map, objectFactory, objectWrapperFactory, reflectorFactory);
    metaObject.setValue("deer",animal);
    System.out.println(map.get("deer").getName()); // map
  }
  @Test
  public void testMap2(){

  }
  @Test
  public void testTokenizer(){
    Animal animal = new Animal();
    animal.setParent(new Animal());
    MetaObject metaObject = MetaObject.forObject(animal, objectFactory, objectWrapperFactory, reflectorFactory);
    metaObject.setValue("parent.name","tokenizer");
    System.out.println(animal.getParent().getName()); // tokenizer
  }

  /**
   * 在例子中,想取出集合的第一个元素怎么办?
   * 并没有输出我们想要的 collection
   * 分析CollectionWrapper的get方法
  */
  @Test
  public void testCollectionGet(){
    Animal animal = new Animal();
    animal.setName("collection");
    List<Animal> list = new ArrayList<>();
    MetaObject metaObject = MetaObject.forObject(list, objectFactory, objectWrapperFactory, reflectorFactory);
    metaObject.add(animal);
    Animal convert = (Animal) metaObject.getValue("[0]");
    System.out.println(convert.getName());
  }

  @Test
  public void testCustomBeanWrapper(){
    Animal animal = new Animal();
    animal.setName("customBeanWrapper");
    List<Animal> list = new ArrayList<>();
    MetaObject metaObject = MetaObject.forObject(list, objectFactory, objectWrapperFactory, reflectorFactory);
    metaObject.add(animal);

    BeanWrapper beanWrapper = new BeanWrapper(metaObject, list);
    Animal convertAnimal = (Animal)beanWrapper.get(new PropertyTokenizer("[0]"));
    System.out.println(convertAnimal.getName());
  }
}
