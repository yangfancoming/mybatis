
package org.apache.ibatis.parsing;

import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.*;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * XpathParser的作用是提供根据Xpath表达式获取基本的DOM节点Node信息的操作
 */
public class XPathParser {

  private static final Log log = LogFactory.getLog(XPathParser.class);

  private final Document document; //Document对象通过createDocument方法得到
  private boolean validation; //是否开启验证
  private EntityResolver entityResolver; //用于加载本地DTD文件，具体实现为XMLMapperEntityResolver类
  private Properties variables; //mybatis-config.xml 中<propteries>标签定义的键值对集合
  private XPath xpath;  //XPath对象

  public XPathParser(String xml) {
    commonConstructor(false, null, null);
    this.document = createDocument(new InputSource(new StringReader(xml)));
  }

  public XPathParser(Reader reader) {
    commonConstructor(false, null, null);
    this.document = createDocument(new InputSource(reader));
  }

  public XPathParser(InputStream inputStream) {
    commonConstructor(false, null, null);
    this.document = createDocument(new InputSource(inputStream));
  }

  public XPathParser(Document document) {
    commonConstructor(false, null, null);
    this.document = document;
  }

  public XPathParser(String xml, boolean validation) {
    commonConstructor(validation, null, null);
    this.document = createDocument(new InputSource(new StringReader(xml)));
  }

  public XPathParser(Reader reader, boolean validation) {
    commonConstructor(validation, null, null);
    this.document = createDocument(new InputSource(reader));
  }

  public XPathParser(InputStream inputStream, boolean validation) {
    commonConstructor(validation, null, null);
    this.document = createDocument(new InputSource(inputStream));
  }

  public XPathParser(Document document, boolean validation) {
    commonConstructor(validation, null, null);
    this.document = document;
  }

  public XPathParser(String xml, boolean validation, Properties variables) {
    commonConstructor(validation, variables, null);
    this.document = createDocument(new InputSource(new StringReader(xml)));
  }

  public XPathParser(Reader reader, boolean validation, Properties variables) {
    commonConstructor(validation, variables, null);
    this.document = createDocument(new InputSource(reader));
  }

  public XPathParser(InputStream inputStream, boolean validation, Properties variables) {
    commonConstructor(validation, variables, null);
    this.document = createDocument(new InputSource(inputStream));
  }

  public XPathParser(Document document, boolean validation, Properties variables) {
    commonConstructor(validation, variables, null);
    this.document = document;
  }

  public XPathParser(String xml, boolean validation, Properties variables, EntityResolver entityResolver) {
    commonConstructor(validation, variables, entityResolver);
    this.document = createDocument(new InputSource(new StringReader(xml)));
  }

  public XPathParser(Reader reader, boolean validation, Properties variables, EntityResolver entityResolver) {
    commonConstructor(validation, variables, entityResolver);
    this.document = createDocument(new InputSource(reader));
  }

  public XPathParser(InputStream inputStream, boolean validation, Properties variables, EntityResolver entityResolver) {
    commonConstructor(validation, variables, entityResolver);
    this.document = createDocument(new InputSource(inputStream));
  }

  public XPathParser(Document document, boolean validation, Properties variables, EntityResolver entityResolver) {
    commonConstructor(validation, variables, entityResolver);
    this.document = document;
  }

  public void setVariables(Properties variables) {
    this.variables = variables;
  }

  /**
   *evalXXX函数有两种多态形式： 一种是只有一个expression参数；
   * @param expression
   */
  public String evalString(String expression) {
    //设置类中的document属性作为root，
    return evalString(document, expression);
  }
  /**
   * evalXXX函数有两种多态形式：除了expression参数外还包含一个root参数。
   * 像我们经常见到的那样，带一个参数的evalXXX函数会在设置一个默认值后调用带有两个参数的函数，
   * @param root
   * @param expression
   */
  public String evalString(Object root, String expression) {
    String result = (String) evaluate(expression, root, XPathConstants.STRING);
    result = PropertyParser.parse(result, variables);
    return result;
  }

  public Boolean evalBoolean(String expression) {
    return evalBoolean(document, expression);
  }

  public Boolean evalBoolean(Object root, String expression) {
    return (Boolean) evaluate(expression, root, XPathConstants.BOOLEAN);
  }

  public Short evalShort(String expression) {
    return evalShort(document, expression);
  }

  public Short evalShort(Object root, String expression) {
    return Short.valueOf(evalString(root, expression));
  }

  public Integer evalInteger(String expression) {
    return evalInteger(document, expression);
  }

  public Integer evalInteger(Object root, String expression) {
    return Integer.valueOf(evalString(root, expression));
  }

  public Long evalLong(String expression) {
    return evalLong(document, expression);
  }

  public Long evalLong(Object root, String expression) {
    return Long.valueOf(evalString(root, expression));
  }

  public Float evalFloat(String expression) {
    return evalFloat(document, expression);
  }

  public Float evalFloat(Object root, String expression) {
    return Float.valueOf(evalString(root, expression));
  }

  public Double evalDouble(String expression) {
    return evalDouble(document, expression);
  }

  public Double evalDouble(Object root, String expression) {
    return (Double) evaluate(expression, root, XPathConstants.NUMBER);
  }

  /* mybatis中都是使用  返回单个节点*/
  public XNode evalNode(String expression) {
    log.debug(  "XPathParser 解析的标签名称为：" + expression);
    return evalNode(document, expression);
  }

  /* 返回单个节点*/
  public XNode evalNode(Object root, String expression) {
    Node node = (Node) evaluate(expression, root, XPathConstants.NODE);
    if (node == null) {
      return null;
    }
    return new XNode(this, node, variables);
  }

  /* 返回多个节点  目前没有使用  只在测试类中  有引用*/
  public List<XNode> evalNodes(String expression) {
    return evalNodes(document, expression);
  }

  /* 返回多个节点 */
  public List<XNode> evalNodes(Object root, String expression) {
    List<XNode> xnodes = new ArrayList<>();
    NodeList nodes = (NodeList) evaluate(expression, root, XPathConstants.NODESET);
    for (int i = 0; i < nodes.getLength(); i++) {
      xnodes.add(new XNode(this, nodes.item(i), variables));
    }
    return xnodes;
  }




  /**
   * XPathConstants.NODE 它主要适用于当XPath表达式的结果有且只有一个节点。
   * 如果XPath表达式返回了多个节点，却指定类型为XPathConstants.NODE，则evaluate()方法将按照文档顺序返回第一个节点。
   * 如果XPath表达式的结果为一个空集，却指定类型为XPathConstants.NODE，则evaluate( )方法将返回null
  */
  private Object evaluate(String expression, Object root, QName returnType) {
    try {
      //调用xpath类进行相应的解析。
      //注意returnType参数，虽然evaluate返回的数据类型是Object的，
      //但是如果指定了错误的returnType，那么在进行类型转换时将会报类型转换异常
      Object evaluate = xpath.evaluate(expression, root, returnType);
      return evaluate;
    } catch (Exception e) {
      throw new BuilderException("Error evaluating XPath.  Cause: " + e, e);
    }
  }

  private Document createDocument(InputSource inputSource) {
    // important: this must only be called AFTER common constructor  注意：此处代码的执行，必须要在调用公共构造函数 以后
    // 那为什么必须在调用commonConstructor函数后才能调用这个函数呢？因为这个函数里面用到了两个属性：validation和entityResolver
    // 如果在这两个属性没有设置前就调用这个函数，就可能会导致这个类内部属性冲突
    try {
      // 调用 DocumentBuilderFactory.newInstance() 方法得到创建 DOM 解析器的工厂
      //创建document时用到了两个类：DocumentBuilderFactory和DocumentBuilder。
      //DocumentBuilderFactory.newInstance()创建DocumentBuilderFactory实现类的对象，它会通过一下方式来查找实现类：
      //1.在系统环境变量中(System.getProperties())中查找 key=javax.xml.parsers.DocumentBuilderFactory
      //2.如果1没有找到，则找java.home/lib/jaxp.properties 文件，如果文件存在，在文件中查找key=javax.xml.parsers.DocumentBuilderFactory
      //3.如果2没有找到,则在classpath中的所有的jar包中查找META-INF/services /javax.xml.parsers.DocumentBuilderFactory 文件
      //  全都没找到，则返回null
      //如果上面都没有找到，那么就使用JDK自带的实现类：
      //com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl
      //在创建DocumentBuilder实例的时候，是根据DocumentBuilderFactoryImpl的不同有不同的实现。
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setValidating(validation);
      factory.setNamespaceAware(false);
      factory.setIgnoringComments(true);
      factory.setIgnoringElementContentWhitespace(false);
      factory.setCoalescing(false);
      factory.setExpandEntityReferences(true);
      // 调用工厂对象的 newDocumentBuilder 方法得到 DOM 解析器对象。
      //XML配置定义文件DTD转换成XMLMapperEntityResolver对象，然后将二者封装到XpathParser对象中，
      //XpathParser的作用是提供根据Xpath表达式获取基本的DOM节点Node信息的操作。
      DocumentBuilder builder = factory.newDocumentBuilder();
      //为了在网络不可用的情况下，正常解析XML文件，我们可以在使用builder之前，设置EntityResolver
      builder.setEntityResolver(entityResolver);
      builder.setErrorHandler(new MyErrorHandler());
      return builder.parse(inputSource);
    } catch (Exception e) {
      throw new BuilderException("Error creating document instance.  Cause: " + e, e);
    }
  }

  private void commonConstructor(boolean validation, Properties variables, EntityResolver entityResolver) {
    //初始化这个类的基本属性
    this.validation = validation;
    this.entityResolver = entityResolver;
    this.variables = variables;
    //利用XPathFactory创建一个新的xpath对象
    this.xpath = XPathFactory.newInstance().newXPath();
  }

}
