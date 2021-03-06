
package org.apache.ibatis.parsing;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.w3c.dom.CharacterData;
import org.w3c.dom.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 该类是对org.w3c.dom.Node类的一个封装，在Node类的基础上添加了一些新功能
 *
 * 这个是mybatis的封装了jdk原有的Node的对象，自己构造出来的，这样子，自己以后的使用中会很方便，不用来回的切换了
 * 这个是xml文件中一个节点的详细信息（注意：mybatis只会读取xml文件，不会写入）
 *
 * XNode做了一下几件事情：
 *              第一件：获取父亲，儿子
 *              第二件：获取一个Node中的属性和对应的值
 *              第三件：获取指定的name和value的值，这个应该是mybatis自己需要的属性
 */

public class XNode {

  private static final Log log = LogFactory.getLog(XNode.class);

  // 被XNode包装的当前节点
  private final Node node;
  // 当前节点名称
  private final String name;
  // 当前节点文本内容
  private final String body;
  // 当前节点所有属性
  private final Properties attributes;
  private final Properties variables;
  private final XPathParser xpathParser;

  /**
   * 使用解析器构造XNode对象，感觉这个对象是一个dom对象，只有一个对象，因为一个xml最好还是个一个dom树最好
   * @param xpathParser xml解析器
   * @param node        节点
   * @param variables   节点的属性
   */
  public XNode(XPathParser xpathParser, Node node, Properties variables) {
    this.xpathParser = xpathParser;
    this.node = node;
    this.name = node.getNodeName(); // 条件断点 this.name.equals("datasource")
    this.variables = variables;
//    log.warn(  "构造函数1745：xpathParser 地址：" + this.xpathParser.hashCode() + "---节点名称：" + this.name);
    // 获取当前节点的所有属性
    this.attributes = parseAttributes(node);
    //获取当前节点的文本节点内容，当然获取到的数据是已经经过TokenHandler处理过的
    this.body = parseBody(node);
  }

  /**
   * 构造一个信息的XNode节点（保存在内存中 ）
   * @param node jdk的一般普通节点，也就是document返回的Node对象，自己进行封装一下
   * @return
   */
  public XNode newXNode(Node node) {
    return new XNode(xpathParser, node, variables);
  }

  /**
   * 获取XNode的父亲（双亲），还是使用原始的node的操作，之后转化成XNode
   * @return 父亲的XNode对象
   */
  public XNode getParent() {
    Node parent = node.getParentNode();
    if (parent == null || !(parent instanceof Element)) {
      return null;
    } else {
      return new XNode(xpathParser, parent, variables);
    }
  }

  /**
   * 获取路径，是xml中的标签的路径
   * @return 路径
   */
  public String getPath() {
    StringBuilder builder = new StringBuilder();
    Node current = node;
    while (current != null && current instanceof Element) {
      if (current != node) {
        builder.insert(0, "/");
      }
      builder.insert(0, current.getNodeName());
      current = current.getParentNode();
    }
    return builder.toString();
  }

  /**
   * 不明白是干什么的，反正是觉得应该是mybatis自己应用解析模式吧
   *  生成元素节点的基础 id
   *  如果是这样子的 XML <employee id="${id_var}">
   *  我们调用     XNode node = parser.evalNode("/employee/height");
   *               node.getValueBasedIdentifier();
   *  则， 返回值应该是   employee[${id_var}]_height
   */
  public String getValueBasedIdentifier() {
    StringBuilder builder = new StringBuilder();
    XNode current = this;
    // 当前的节点不为空
    while (current != null) {
      // 如果节点不等于 this， 则在0之前插入 _ 符号， 因为是不断的获取父节点的， 因此是插在前面
      if (current != this) {
        builder.insert(0, "_");
      }
      // 获取 id， id不存在则获取value, value不存在则获取 property。
      String value = current.getStringAttribute("id", current.getStringAttribute("value", current.getStringAttribute("property", null)));
      // value 非空， 则将.替换为_， 并将value的值加上 []
      if (value != null) {
        value = value.replace('.', '_');
        // 注意StringBuilder使用的插入模式，好像这个是的动态的字符串，，可以随便的插入和删除操作，很方便
        builder.insert(0, "]");
        builder.insert(0,  value);
        builder.insert(0, "[");
      }
      // 不管 value 是否存在， 前面都添加上节点的名称
      builder.insert(0, current.getName());
      // 获取父节点
      current = current.getParent();
    }
    return builder.toString();
  }

  /**
   * 下面是引用 XPathParse 的一些解析方法
   * @param expression 解析的表达式
   * @return 解析之后的值
   */
  public String evalString(String expression) {
    return xpathParser.evalString(node, expression);
  }

  public Boolean evalBoolean(String expression) {
    return xpathParser.evalBoolean(node, expression);
  }

  public Double evalDouble(String expression) {
    return xpathParser.evalDouble(node, expression);
  }

  public List<XNode> evalNodes(String expression) {
    return xpathParser.evalNodes(node, expression);
  }

  public XNode evalNode(String expression) {
    log.warn(  " 解析的标签名称为：" + expression);
    return xpathParser.evalNode(node, expression);
  }

  public Node getNode() {
    return node;
  }

  public String getName() {
    return name;
  }

  public String getStringBody() {
    return getStringBody(null);
  }

  public String getStringBody(String def) {
    return (body == null) ? def : body; // -modify
  }

  public Boolean getBooleanBody() {
    return getBooleanBody(null);
  }

  /**
   * 通过getXXXBody函数获取body属性并将其转换为对应的数据类型
   * @param def
   */
  public Boolean getBooleanBody(Boolean def) {
    //两个函数的不同在于这个函数具有一个默认值，而上面的没有
    return (body == null) ? def : Boolean.valueOf(Boolean.valueOf(body)); // -modify
  }

  public Integer getIntBody() {
    return getIntBody(null);
  }

  public Integer getIntBody(Integer def) {
    return (body == null) ? def : Integer.valueOf(Integer.parseInt(body)); // -modify
  }

  public Long getLongBody() {
    return getLongBody(null);
  }

  public Long getLongBody(Long def) {
    return (body == null) ? def : Long.valueOf(Long.parseLong(body)); // -modify
  }

  public Double getDoubleBody() {
    return getDoubleBody(null);
  }

  public Double getDoubleBody(Double def) {
    return (body == null) ? def : Double.valueOf(Double.parseDouble(body)); // -modify
  }

  public Float getFloatBody() {
    return getFloatBody(null);
  }

  public Float getFloatBody(Float def) {
    return (body == null) ? def : Float.valueOf(Float.parseFloat(body)); // -modify
  }

  public <T extends Enum<T>> T getEnumAttribute(Class<T> enumType, String name) {
    return getEnumAttribute(enumType, name, null);
  }

  public <T extends Enum<T>> T getEnumAttribute(Class<T> enumType, String name, T def) {
    String value = getStringAttribute(name);
    return (value == null) ? def : Enum.valueOf(enumType, value); // -modify
  }

  public String getStringAttribute(String name) {
    return getStringAttribute(name, null);
  }

  public String getStringAttribute(String name, String def) {
    String value = attributes.getProperty(name);
    return (value == null) ? def : value; // -modify
  }

  public Boolean getBooleanAttribute(String name) {
    return getBooleanAttribute(name, null);
  }

  /**
   * 通过getXXXAttribute获取value值
   * @param name
   * @param def
   */
  public Boolean getBooleanAttribute(String name, Boolean def) {
    //从attributes获取key，如果存在则进行类型转换，否则就返回默认值
    String value = attributes.getProperty(name);
    return (value == null) ? def : Boolean.valueOf(Boolean.valueOf(value)); // -modify
  }

  public Integer getIntAttribute(String name) {
    return getIntAttribute(name, null);
  }

  public Integer getIntAttribute(String name, Integer def) {
    String value = attributes.getProperty(name);
    return (value == null) ? def : Integer.valueOf(Integer.parseInt(value)); // -modify
  }

  public Long getLongAttribute(String name) {
    return getLongAttribute(name, null);
  }

  public Long getLongAttribute(String name, Long def) {
    String value = attributes.getProperty(name);
    // sos 这里千万不能使用  Long.parseLong(value) 否则自动拆箱后会报空异常，所以必须要装箱和def保持类型一样
    return (value == null) ? def : Long.valueOf(Long.parseLong(value)); // -modify
  }

  public Double getDoubleAttribute(String name) {
    return getDoubleAttribute(name, null);
  }

  public Double getDoubleAttribute(String name, Double def) {
    String value = attributes.getProperty(name);
    return (value == null) ? def : Double.valueOf(Double.parseDouble(value)); // -modify
  }

  public Float getFloatAttribute(String name) {
    return getFloatAttribute(name, null);
  }

  public Float getFloatAttribute(String name, Float def) {
    String value = attributes.getProperty(name);
    return (value == null) ? def : Float.valueOf(Float.parseFloat(value)); // -modify
  }

  /**
   * 获取当前节点的所有孩子节点(List集合)
   */
  public List<XNode> getChildren() {
    List<XNode> children = new ArrayList<>();
    //获取所有子节点
    NodeList nodeList = node.getChildNodes();
    if (nodeList != null) {
      for (int i = 0, n = nodeList.getLength(); i < n; i++) {
        Node node = nodeList.item(i);
        //如果子节点类型是元素节点，就添加到list中
        if (node.getNodeType() == Node.ELEMENT_NODE) {
          // 将节点对象封装到 XNode 中，并将 XNode 对象放入 children 列表中
          children.add(new XNode(xpathParser, node, variables));
        }
      }
    }
    return children;
  }

  /**
   * @Description: 获取 当前节点的所有子节点 并将这些子节点 两个属性和对应的属性值 保存到Properties对象
   *   <settings>
   *     <setting name="mapUnderscoreToCamelCase" value="true"/>
   *     <setting name="cacheEnabled" value="true" />
   *   </settings>
   * 输入示例：   getChildrenAsProperties("name", "value");
   * 输出结果：   key：mapUnderscoreToCamelCase  value：true
   * 输出结果：   key：cacheEnabled              value：true
   * @date 2018/7/11
   * @return  Properties
   */
  public Properties getChildrenAsProperties() {
    Properties properties = new Properties();
    List<XNode> childrens = getChildren();
    for (XNode child : childrens) {
      // 获取 <property> 节点的 name 和 value 属性
      String name = child.getStringAttribute("name");
      String value = child.getStringAttribute("value");
      // 只有同时具有name和value属性的节点，才会被添加到properties中
      if (name != null && value != null) {
        properties.setProperty(name, value);
      }
    }
    return properties;
  }

  // 给了一个toString方法，这样子在显示的时候更加友好
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("<");
    builder.append(name);
    for (Map.Entry<Object, Object> entry : attributes.entrySet()) {
      builder.append(" ");
      builder.append(entry.getKey());
      builder.append("=\"");
      builder.append(entry.getValue());
      builder.append("\"");
    }
    List<XNode> children = getChildren();
    if (!children.isEmpty()) {
      builder.append(">\n");
      for (XNode node : children) {
        builder.append(node.toString());
      }
      builder.append("</");
      builder.append(name);
      builder.append(">");
    } else if (body != null) {
      builder.append(">");
      builder.append(body);
      builder.append("</");
      builder.append(name);
      builder.append(">");
    } else {
      builder.append("/>");
    }
    builder.append("\n");
    return builder.toString();
  }

  /**
   * 获取当前节点的所有属性，就是你配置在这个节点中的属性，不是text内容(XNode并没有采用一种继承的策略，而是一种组合的关系，这样子最好了)
   * @param n 节点
   * @return 节点的属性内容
   */
  private Properties parseAttributes(Node n) {
    // 获取Node之中的属性，然后是进行遍历属性获取内容
    Properties attributes = new Properties();
    NamedNodeMap attributeNodes = n.getAttributes();
    if (attributeNodes != null) {
      for (int i = 0; i < attributeNodes.getLength(); i++) {
        Node attribute = attributeNodes.item(i);
        // 就是利用Node类的函数去获取该节点的所有属性名和值，只是在获取属性值后会调用PropertyParser.parse()去处理下
        String value = PropertyParser.parse(attribute.getNodeValue(), variables);
//        log.warn(  " parseAttributes()：标签名：<" +  StringUtils.rightPad(n.getNodeName() + ">", 20) + " \t 属性值：" + value);
        attributes.put(attribute.getNodeName(), value);
      }
    }
    return attributes;
  }

  /**
   * 解析身体的东西，不知道干什么用,反正是递归的调用这个东西
   *  获取当前节点的文本节点内容，当然获取到的数据是已经经过TokenHandler处理过的
   * @param node
   */
  private String parseBody(Node node) {
    String data = getBodyData(node);
    //如果该节点不是文本节点或者CDATA节点，取其子节点值
    if (data == null) {
      NodeList children = node.getChildNodes();
      //尽管这个for循环不是一个好的实现方式，因为 children.getLength()被执行了多次，但在mybatis的源代码经常出现
      for (int i = 0; i < children.getLength(); i++) {
        Node child = children.item(i);
        data = getBodyData(child);
        //只要一个节点为文本节点或者CDATA节点,就结束循环。因而此时的body值只是node的第一个文本节点的内容
        if (data != null) {
          break;
        }
      }
    }
    return data;
  }

  /**
   * 如果这个节点是文本节点或者CDATA节点，获取其节点值，然后再用PropertyParser.parse() 处理下
   * @param child
   */
  private String getBodyData(Node child) {
    if (child.getNodeType() == Node.CDATA_SECTION_NODE || child.getNodeType() == Node.TEXT_NODE) {
      String data = ((CharacterData) child).getData();
      data = PropertyParser.parse(data, variables);
      return data;
    }
    return null;
  }
}
