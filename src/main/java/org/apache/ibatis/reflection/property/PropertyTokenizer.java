
package org.apache.ibatis.reflection.property;

import java.util.Iterator;

/**
 <resultMap id="map" type="Order">
   <result property="orders[0].items[0].name" column="col1"/>
   <result property="orders[0].items[1].name" column="col2"/>
   ...
 </resultMap>
 orders[0].items[0].name 这样的表达式是由 PropertyTokenizer 解析的，其构造方法能够对表达式进行解析；
 同时还实现了 Iterator 接口，能够迭代解析表达式  该类会被reflection包中其他的类频繁的引用到。
 这个类实现了Iterator接口，在使用时经常被用到的是Iterator接口中的hasNext这个函数
*/
public class PropertyTokenizer implements Iterator<PropertyTokenizer> {
  private String name;
  private final String indexedName;
  private String index;
  private final String children;

  // 可以看到，这个类传入一个字符串到构造函数，然后提供了iterator方法对解析后的子串进行遍历，是一个很常用的方法类。
  public PropertyTokenizer(String fullname) {
    // 检测传入的参数中是否包含字符 '.'
    int delim = fullname.indexOf('.');
    if (delim > -1) {
      /*
       * 以点位为界，进行分割  比如： fullname = www.coolblog.xyz
       * 以第一个点为分界符： name = www  children = coolblog.xyz
       */
      name = fullname.substring(0, delim);
      children = fullname.substring(delim + 1);
    } else {
      // fullname 中不存在字符 '.'
      name = fullname;
      children = null;
    }
    indexedName = name;
    // 检测传入的参数中是否包含字符 '['
    delim = name.indexOf('[');
    if (delim > -1) {
      /*
       * 获取中括号里的内容，比如：
       * 1. 对于数组或 List 集合： [] 中的内容为数组下标， 比如 fullname = articles[1]， index = 1
       * 2. 对于 Map： [] 中的内容为键，比如 fullname = xxxMap[keyName]， index = keyName
       */
      index = name.substring(delim + 1, name.length() - 1);
      // 获取分解符前面的内容，比如 fullname = articles[1]， name = articles
      name = name.substring(0, delim);
    }
  }

  public String getName() {
    return name;
  }

  public String getIndex() {
    return index;
  }

  public String getIndexedName() {
    return indexedName;
  }

  public String getChildren() {
    return children;
  }

  // 是否有children表达式继续迭代
  @Override
  public boolean hasNext() {
    return children != null;
  }

  // 对 children 进行再次切分，用于解析多重复合属性  //分解出的 . 分隔符的 children 表达式可以继续迭代
  @Override
  public PropertyTokenizer next() {
    return new PropertyTokenizer(children);
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException("Remove is not supported, as it has no meaning in the context of properties.");
  }
}
