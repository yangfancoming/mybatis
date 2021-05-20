
package org.apache.ibatis.builder.xml;

import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.builder.IncompleteElementException;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.parsing.PropertyParser;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.session.Configuration;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * XML include转换器
 */
public class XMLIncludeTransformer {

  private static final Log log = LogFactory.getLog(XMLIncludeTransformer.class);

  private final Configuration configuration;
  private final MapperBuilderAssistant builderAssistant;

  public XMLIncludeTransformer(Configuration configuration, MapperBuilderAssistant builderAssistant) {
    this.configuration = configuration;
    this.builderAssistant = builderAssistant;
    log.warn("构造函数 202001071554：configuration 地址：" + configuration);
    log.warn("构造函数 202001071554：builderAssistant 地址：" + builderAssistant);
  }

  // 从 parseStatementNode 方法进入时， Node 还是 （select|insert|update|delete） 节点
  public void applyIncludes(Node source) {
    Properties variablesContext = new Properties();
    // 获取的是 mybatis-config.xml 所定义的属性，这些属性后续在将 ${XXX} 替换成真实的参数时会用到。
    Properties configurationVariables = configuration.getVariables();
    Optional.ofNullable(configurationVariables).ifPresent(variablesContext::putAll);
    // 处理 <include> 子节点
    applyIncludes(source, variablesContext, false);
  }

  /**
   * 然后递归解析所有的 include 节点。
   * Recursively apply includes through all SQL fragments.
   * @param source Include node in DOM tree
   * @param variablesContext Current context for static variables with values
   */
  private void applyIncludes(Node source, final Properties variablesContext, boolean included) {
    // 拿到SQL片段  走到这里，单独解析<include refid="userColumns"/>
    if (source.getNodeName().equals("include")) {
      // 查找 refid 属性指向 <sql> 节点
      Node toInclude = findSqlFragment(getStringAttribute(source, "refid"), variablesContext);
      // 获取<include>标签中的<property>中的值:{tableName=author}
      // 解析 <include> 节点下的 <property> 节点， 将得到的键值对添加到 variablesContext 中,并形成 Properties 对象返回， 用于替换占位符
      Properties toIncludeContext = getVariablesContext(source, variablesContext);
      // 递归调用applyIncludes，会进入2中为 Node attr设置table_name,接着会进入4再次递归，这次递归进入3，为tableName设值author
      // 递归处理 <include> 节点， 在 <sql> 节点中可能会 <include> 其他 SQL 片段
      applyIncludes(toInclude, toIncludeContext, true);
      if (toInclude.getOwnerDocument() != source.getOwnerDocument()) {
        toInclude = source.getOwnerDocument().importNode(toInclude, true);
      }
      // 将include节点，替换为sqlFragment节点
      source.getParentNode().replaceChild(toInclude, source);
      while (toInclude.hasChildNodes()) {
        // 将sqlFragment的子节点（也就是文本节点），插入到sqlFragment的前面
        toInclude.getParentNode().insertBefore(toInclude.getFirstChild(), toInclude);
      }
      // 移除sqlFragment节点
      toInclude.getParentNode().removeChild(toInclude);
    } else if (source.getNodeType() == Node.ELEMENT_NODE) {
      if (included && !variablesContext.isEmpty()) {
        // replace variables in attribute values
        //  获取所有的属性值， 并使用 variablesContext 进行占位符的解析
        NamedNodeMap attributes = source.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
          Node attr = attributes.item(i);
          attr.setNodeValue(PropertyParser.parse(attr.getNodeValue(), variablesContext));
        }
      }
      // 获取所有的子类， 并递归解析
      NodeList children = source.getChildNodes();
      for (int i = 0; i < children.getLength(); i++) {
        applyIncludes(children.item(i), variablesContext, included);
      }
    } else if (included && source.getNodeType() == Node.TEXT_NODE && !variablesContext.isEmpty()) {
      // replace variables in text node // 使用 variablesContext 进行占位符的解析
      source.setNodeValue(PropertyParser.parse(source.getNodeValue(), variablesContext));
    }
  }

  private Node findSqlFragment(String refid, Properties variables) {
    refid = PropertyParser.parse(refid, variables);
    refid = builderAssistant.applyCurrentNamespace(refid, true);
    try {
      // 去之前存到内存map的SQL片段中寻找
      XNode nodeToInclude = configuration.getSqlFragments().get(refid);
      // clone一下，以防改写？
      return nodeToInclude.getNode().cloneNode(true);
    } catch (IllegalArgumentException e) {
      throw new IncompleteElementException("Could not find SQL statement to include with refid '" + refid + "'", e);
    }
  }

  private String getStringAttribute(Node node, String name) {
    return node.getAttributes().getNamedItem(name).getNodeValue();
  }

  /**
   * Read placeholders and their values from include node definition.
   * @param node Include node instance
   * @param inheritedVariablesContext Current context used for replace variables in new variables values
   * @return variables context from include instance (no inherited values)
   */
  private Properties getVariablesContext(Node node, Properties inheritedVariablesContext) {
    Map<String, String> declaredProperties = null;
    NodeList children = node.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Node n = children.item(i);
      if (n.getNodeType() == Node.ELEMENT_NODE) {
        String name = getStringAttribute(n, "name");
        // Replace variables inside
        String value = PropertyParser.parse(getStringAttribute(n, "value"), inheritedVariablesContext);
        if (declaredProperties == null) declaredProperties = new HashMap<>();
        if (declaredProperties.put(name, value) != null) {
          throw new BuilderException("Variable " + name + " defined twice in the same include definition");
        }
      }
    }
    if (declaredProperties == null) {
      return inheritedVariablesContext;
    } else {
      Properties newProperties = new Properties();
      newProperties.putAll(inheritedVariablesContext);
      newProperties.putAll(declaredProperties);
      return newProperties;
    }
  }
}
