
package org.apache.ibatis.scripting.xmltags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.builder.BaseBuilder;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.scripting.defaults.RawSqlSource;
import org.apache.ibatis.session.Configuration;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 这个XMLScriptBuilder类才是真正负责在背后解析mapper文件中的每个<select/>,<insert/>,<update/>,<delete/>节点内的SQL字符串(其中可能包含动态SQL部分,诸如<if/>,<where/>等) 的功臣.
*/
public class XMLScriptBuilder extends BaseBuilder {

  private static final Log log = LogFactory.getLog(XMLConfigBuilder.class);

  private final XNode context;
  private boolean isDynamic;
  private final Class<?> parameterType;
  // <!ELEMENT select (#PCDATA | include | trim | where | set | foreach | choose | if | bind)*>
  // key为 子标签  value为 对应的处理器
  private final Map<String, NodeHandler> nodeHandlerMap = new HashMap<>();

  public XMLScriptBuilder(Configuration configuration, XNode context) {
    this(configuration, context, null);
  }

  public XMLScriptBuilder(Configuration configuration, XNode context, Class<?> parameterType) {
    super(configuration);
    this.context = context;
    this.parameterType = parameterType;
    log.warn("构造函数 202001071654：XNode 地址：" + context.hashCode() + "节点名称：" + context.getName());
    log.warn("构造函数 202001071654：configuration 地址：" + configuration);
    initNodeHandlerMap();
  }

  // doit  这里为什么不用 静态代码块  或是静态的map呢？
  private void initNodeHandlerMap() {
    nodeHandlerMap.put("trim", new TrimHandler());
    nodeHandlerMap.put("where", new WhereHandler());
    nodeHandlerMap.put("set", new SetHandler());
    nodeHandlerMap.put("foreach", new ForEachHandler());
    nodeHandlerMap.put("if", new IfHandler());
    nodeHandlerMap.put("choose", new ChooseHandler());
    nodeHandlerMap.put("when", new IfHandler());
    nodeHandlerMap.put("otherwise", new OtherwiseHandler());
    nodeHandlerMap.put("bind", new BindHandler());
  }

  public SqlSource parseScriptNode() {
    /**
     *     解析SQL语句节点，创建MixedSqlNode对象
     *     获取select/update/insert/delete节点下的Sql语句节点包装成相应的SqlNode对象，每个CRUD语句 可能都有多个SqlNode对象
     *     包装成混合型SqlNode对象，'Mixed'译为混合，很贴切的名字
     *
     * 传入的context参数为：
     * <select resultType="org.apache.goat.common.Customer" id="getTest">
     *    <if test="id!=null">
     *         and id = #{id}
     *    </if>
     * </select>
    */
    MixedSqlNode rootSqlNode = parseDynamicTags(context);
    // 为SqlSource接口 选定实现类 动态版的 DynamicSqlSource 或 原生版的 RawSqlSource
    SqlSource sqlSource = isDynamic ? new DynamicSqlSource(configuration, rootSqlNode) : new RawSqlSource(configuration, rootSqlNode, parameterType);// -modify
    log.warn("parseScriptNode()：为 SqlSource 接口选定 实现类：" + sqlSource.getClass());
    return sqlSource;
  }

  /**
   * 解析sql语句
   * node是我们要解析的SQL语句: <select id="selectAllAuthors" resultType="org.apache.ibatis.domain.blog.Author" >select * from author</select>
   *   <select id="selectById" parameterType="int" resultType="org.apache.goat.common.Foo" >
   *     select * from foo where id = #{id}
   *   </select>
  */
  protected MixedSqlNode parseDynamicTags(XNode node) {
    // 获取CRUD节点下所有子节点，包括文本内容<trim>等动态sql节点
    List<SqlNode> contents = new ArrayList<>();
    // 获取SQL下面的子节点
    NodeList children = node.getNode().getChildNodes(); // 这里的children只有一个节点；
    // 遍历子节点，解析成对应的sqlNode类型，并添加到contents中
    for (int i = 0; i < children.getLength(); i++) {
      XNode child = node.newXNode(children.item(i));
      log.warn("开始解析 crud 标签下的所有动态标签  XNode 地址：" + StringUtils.rightPad(child.hashCode()+"", 15)  + "节点名称：" + child.getName());
      // 如果是文本节点，则先解析成TextSqlNode对象
      if (child.getNode().getNodeType() == Node.CDATA_SECTION_NODE || child.getNode().getNodeType() == Node.TEXT_NODE) {
        // 直接获取数据内容，类似"select * from .."纯文本语句 //获取文本信息
        String data = child.getStringBody("");//  select * from customers where 1=1
        TextSqlNode textSqlNode = new TextSqlNode(data);   //创建TextSqlNode对象
        // 判断是否是动态Sql，检查纯文本语句是否含有 "${ " 字符串，有则为true。 eg: "#{ " 则为false
        if (textSqlNode.isDynamic()) {
          contents.add(textSqlNode);
          isDynamic = true;//如果是动态SQL,则直接使用TextSqlNode类型，并将isDynamic标识置为true
          log.warn("发现<动态>文本类型节点，节点内容：" + data.replaceAll("\n",""));
        } else { // 不是动态sql，则创建StaticTextSqlNode对象，表示静态SQL
          // 返回最普通的含有data的StaticTextSqlNode对象
          contents.add(new StaticTextSqlNode(data));
          log.warn("发现<静态>文本类型节点，节点内容：" + data.replaceAll("\n",""));
        }
      } else if (child.getNode().getNodeType() == Node.ELEMENT_NODE) { // issue #628 //其他类型的节点，由不同的节点处理器来对应处理成本成不同的SqlNode类型
        String nodeName = child.getNode().getNodeName();
        log.warn("发现节点类型节点，节点名称：" + nodeName );
        // 策略模式： 获取对应 initNodeHandlerMap() 填充的节点处理器策略
        NodeHandler handler = nodeHandlerMap.get(nodeName);
        if (handler == null) throw new BuilderException("Unknown element <" + nodeName + "> in SQL statement.");
        // 调用同一接口实现解析生成SqlNode对象并统一存入List<SqlNode>集合中
        // 这里就相当于递归了！！！
        handler.handleNode(child, contents);
        isDynamic = true;
      }
    }
    // 用contents构建MixedSqlNode对象
    return new MixedSqlNode(contents);
  }

  /** 主要应用于对trim/where/set/if等类型为ELEMENT节点的补充解析。 */
  private interface NodeHandler {
    void handleNode(XNode nodeToHandle, List<SqlNode> targetContents);
  }

  /**  用于解析bind标签节点 */
  private class BindHandler implements NodeHandler {
    public BindHandler() {
      // Prevent Synthetic Access
    }
    @Override
    public void handleNode(XNode nodeToHandle, List<SqlNode> targetContents) {
      // 获取name和value属性
      final String name = nodeToHandle.getStringAttribute("name");
      final String expression = nodeToHandle.getStringAttribute("value");
      // 包装成简单的VarDeclSqlNode类
      final VarDeclSqlNode node = new VarDeclSqlNode(name, expression);
      targetContents.add(node);
    }
  }
  /**  用于解析trim标签节点 */
  private class TrimHandler implements NodeHandler {
    public TrimHandler() {
      // Prevent Synthetic Access
    }

    @Override
    public void handleNode(XNode nodeToHandle, List<SqlNode> targetContents) {
      // trim标签下可包含where/set/if/when等标签，将之封装成MixedSqlNode
      MixedSqlNode mixedSqlNode = parseDynamicTags(nodeToHandle);
      // read prefix/preffixOverrides/suffix/suffixOverrides properties
      String prefix = nodeToHandle.getStringAttribute("prefix");
      String prefixOverrides = nodeToHandle.getStringAttribute("prefixOverrides");
      String suffix = nodeToHandle.getStringAttribute("suffix");
      String suffixOverrides = nodeToHandle.getStringAttribute("suffixOverrides");
      // delegate TrimSqlNode to process trim sql
      TrimSqlNode trim = new TrimSqlNode(configuration, mixedSqlNode, prefix, prefixOverrides, suffix, suffixOverrides);
      targetContents.add(trim);
    }
  }
  /**  用于解析 where 标签节点 */
  private class WhereHandler implements NodeHandler {
    public WhereHandler() {
      // Prevent Synthetic Access
    }

    @Override
    public void handleNode(XNode nodeToHandle, List<SqlNode> targetContents) {
      MixedSqlNode mixedSqlNode = parseDynamicTags(nodeToHandle);
      // same as TrimSqlNode
      WhereSqlNode where = new WhereSqlNode(configuration, mixedSqlNode);
      targetContents.add(where);
    }
  }
  /**  用于解析 set 标签节点 */
  private class SetHandler implements NodeHandler {
    public SetHandler() {
      // Prevent Synthetic Access
    }

    @Override
    public void handleNode(XNode nodeToHandle, List<SqlNode> targetContents) {
      MixedSqlNode mixedSqlNode = parseDynamicTags(nodeToHandle);
      SetSqlNode set = new SetSqlNode(configuration, mixedSqlNode);
      targetContents.add(set);
    }
  }
  /**  用于解析 foreach 标签节点 */
  private class ForEachHandler implements NodeHandler {
    public ForEachHandler() {
      // Prevent Synthetic Access
    }

    /**
     foreach节点的属性简析如下
     collection 代表的是集合的类型,例如list代表参数类型为List.class,array代表参数类型为数组类型
     item 代表集合的value值
     index 代表集合的key值，可为下标值也可为HashMap中的key值
     open 类似于prefix属性
     close 类似于suffix属性
     separator 拼装的分隔符号,多为','
    */
    @Override
    public void handleNode(XNode nodeToHandle, List<SqlNode> targetContents) {
      MixedSqlNode mixedSqlNode = parseDynamicTags(nodeToHandle);
      String collection = nodeToHandle.getStringAttribute("collection");
      String item = nodeToHandle.getStringAttribute("item");
      String index = nodeToHandle.getStringAttribute("index");
      String open = nodeToHandle.getStringAttribute("open");
      String close = nodeToHandle.getStringAttribute("close");
      String separator = nodeToHandle.getStringAttribute("separator");
      ForEachSqlNode forEachSqlNode = new ForEachSqlNode(configuration, mixedSqlNode, collection, index, item, open, close, separator);
      targetContents.add(forEachSqlNode);
    }
  }
  /**  用于解析 <if> 标签节点 */
  private class IfHandler implements NodeHandler {
    public IfHandler() {
      // Prevent Synthetic Access
    }

    @Override
    public void handleNode(XNode nodeToHandle, List<SqlNode> targetContents) {
      MixedSqlNode mixedSqlNode = parseDynamicTags(nodeToHandle);
      String test = nodeToHandle.getStringAttribute("test");
      IfSqlNode ifSqlNode = new IfSqlNode(mixedSqlNode, test);
      targetContents.add(ifSqlNode);
    }
  }

  private class OtherwiseHandler implements NodeHandler {
    public OtherwiseHandler() {
      // Prevent Synthetic Access
    }

    @Override
    public void handleNode(XNode nodeToHandle, List<SqlNode> targetContents) {
      MixedSqlNode mixedSqlNode = parseDynamicTags(nodeToHandle);
      targetContents.add(mixedSqlNode);
    }
  }

  private class ChooseHandler implements NodeHandler {
    public ChooseHandler() {
      // Prevent Synthetic Access
    }

    @Override
    public void handleNode(XNode nodeToHandle, List<SqlNode> targetContents) {
      List<SqlNode> whenSqlNodes = new ArrayList<>();
      List<SqlNode> otherwiseSqlNodes = new ArrayList<>();
      // 解析choose...when..otherwise结构
      handleWhenOtherwiseNodes(nodeToHandle, whenSqlNodes, otherwiseSqlNodes);
      // 检查otherwise标签是否只有一个，大于一个则报错
      SqlNode defaultSqlNode = getDefaultSqlNode(otherwiseSqlNodes);
      ChooseSqlNode chooseSqlNode = new ChooseSqlNode(whenSqlNodes, defaultSqlNode);
      targetContents.add(chooseSqlNode);
    }
    // when标签使用IfHandler解析，otherwise标签使用OtherwiseHandler
    private void handleWhenOtherwiseNodes(XNode chooseSqlNode, List<SqlNode> ifSqlNodes, List<SqlNode> defaultSqlNodes) {
      List<XNode> children = chooseSqlNode.getChildren();
      for (XNode child : children) {
        String nodeName = child.getNode().getNodeName();
        NodeHandler handler = nodeHandlerMap.get(nodeName);
        if (handler instanceof IfHandler) {
          handler.handleNode(child, ifSqlNodes);
        } else if (handler instanceof OtherwiseHandler) {
          handler.handleNode(child, defaultSqlNodes);
        }
      }
    }

    private SqlNode getDefaultSqlNode(List<SqlNode> defaultSqlNodes) {
      SqlNode defaultSqlNode = null;
      if (defaultSqlNodes.size() == 1) {
        defaultSqlNode = defaultSqlNodes.get(0);
      } else if (defaultSqlNodes.size() > 1) {
        throw new BuilderException("Too many default (otherwise) elements in choose statement.");
      }
      return defaultSqlNode;
    }
  }
}
