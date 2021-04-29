
package org.apache.ibatis.binding;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.ibatis.annotations.Flush;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ParamNameResolver;
import org.apache.ibatis.reflection.TypeParameterResolver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;

/**
 * MapperMethod 代理Mapper所有方法
 *
 * MapperMethod 的功能：
 * 1. 解析Mapper接口的方法，并封装成 MapperMethod 对象。
 * 2. 将Sql命令，正确路由到恰当的SqlSession的方法上。
 */
public class MapperMethod {

  // 一个内部类  SqlCommand表示该sql的类型，一般为select|update|insert|delete|flush等类型
  private final SqlCommand command;
  // 一个内部类  method适配器，一般解析mapper接口对应method的参数集合以及回参等
  private final MethodSignature method;

  public MapperMethod(Class<?> mapperInterface, Method method, Configuration config) {
    // 创建 SqlCommand 对象，该对象包含一些和 SQL 相关的信息
    command = new SqlCommand(config, mapperInterface, method);
    // 创建 MethodSignature 对象， 由类名可知，该对象包含了被拦截方法的一些信息
    this.method = new MethodSignature(config, mapperInterface, method);
  }

 // 这个方法是对SqlSession的包装 可以看到执行时就是4种情况，insert|update|delete|select，分别调用SqlSession的4大类方法
  public Object execute(SqlSession sqlSession, Object[] args) {
    Object result;
    // CURD操作，对持久层返回的结果集进行处理  获取method方法上的带有@Param的参数，默认返回0,1,2,3...
    switch (command.getType()) {
      case INSERT: {
        Object param = method.convertArgsToSqlCommandParam(args);
        result = rowCountResult(sqlSession.insert(command.getName(), param));
        break;
      }
      case UPDATE: {
        Object param = method.convertArgsToSqlCommandParam(args);
        result = rowCountResult(sqlSession.update(command.getName(), param));
        break;
      }
      case DELETE: {
        Object param = method.convertArgsToSqlCommandParam(args);
        result = rowCountResult(sqlSession.delete(command.getName(), param));
        break;
      }
      // 查询语句的各种情况应对
      case SELECT:
        // 如果返回void 并且参数有resultHandler  ,则调用 void select(String statement, Object parameter, ResultHandler handler);方法
        if (method.returnsVoid() && method.hasResultHandler()) {
          // 如果有结果处理器
          executeWithResultHandler(sqlSession, args);
          result = null;
        } else if (method.returnsMany()) {
          // 如果返回多行结果,executeForMany这个方法调用 <E> List<E> selectList(String statement, Object parameter);
          // 如果结果有多条记录
          result = executeForMany(sqlSession, args);
        } else if (method.returnsMap()) {
          // 如果返回类型是MAP 则调用executeForMap方法
          result = executeForMap(sqlSession, args);
        } else if (method.returnsCursor()) {
          result = executeForCursor(sqlSession, args);
        } else {
          // 否则就是查询单个对象  一条记录
          Object param = method.convertArgsToSqlCommandParam(args); // doit  这行代码 四种情况都会用到为啥不拿出来放在本函数的第一行？？？
          result = sqlSession.selectOne(command.getName(), param);
          if (method.returnsOptional() && (result == null || !method.getReturnType().equals(result.getClass()))) {
            result = Optional.ofNullable(result);
          }
        }
        break;
      case FLUSH:
        result = sqlSession.flushStatements();
        break;
      default:
        // 接口方法没有和sql命令绑定
        throw new BindingException("Unknown execution method for: " + command.getName());
    }
    // 如果返回值为空 并且方法返回值类型是基础类型 并且不是VOID 则抛出异常
    if (result == null && method.getReturnType().isPrimitive() && !method.returnsVoid()) {
      throw new BindingException("Mapper method '" + command.getName() + " attempted to return null from a method with a primitive return type (" + method.getReturnType() + ").");
    }
    return result;
  }

  // 这个方法对返回值的类型进行了一些检查，使得更安全
  private Object rowCountResult(int rowCount) {
    final Object result;
    if (method.returnsVoid()) {
      result = null;
    } else if (Integer.class.equals(method.getReturnType()) || Integer.TYPE.equals(method.getReturnType())) {
      // 如果返回值是大int或小int
      result = rowCount;
    } else if (Long.class.equals(method.getReturnType()) || Long.TYPE.equals(method.getReturnType())) {
      // 如果返回值是大long或小long
      result = (long)rowCount;
    } else if (Boolean.class.equals(method.getReturnType()) || Boolean.TYPE.equals(method.getReturnType())) {
      // 如果返回值是大boolean或小boolean
      result = rowCount > 0;
    } else {
      throw new BindingException("Mapper method '" + command.getName() + "' has an unsupported return type: " + method.getReturnType());
    }
    return result;
  }
  // 结果处理器
  private void executeWithResultHandler(SqlSession sqlSession, Object[] args) {
    MappedStatement ms = sqlSession.getConfiguration().getMappedStatement(command.getName());
    if (!StatementType.CALLABLE.equals(ms.getStatementType()) && void.class.equals(ms.getResultMaps().get(0).getType())) {
      throw new BindingException("method " + command.getName()
          + " needs either a @ResultMap annotation, a @ResultType annotation, or a resultType attribute in XML so a ResultHandler can be used as a parameter.");
    }
    Object param = method.convertArgsToSqlCommandParam(args);
    if (method.hasRowBounds()) {
      RowBounds rowBounds = method.extractRowBounds(args);
      sqlSession.select(command.getName(), param, rowBounds, method.extractResultHandler(args));
    } else {
      sqlSession.select(command.getName(), param, method.extractResultHandler(args));
    }
  }

  private <K, V> Map<K, V> executeForMap(SqlSession sqlSession, Object[] args) {
    Map<K, V> result;
    Object param = method.convertArgsToSqlCommandParam(args);
    if (method.hasRowBounds()) {
      RowBounds rowBounds = method.extractRowBounds(args);
      result = sqlSession.selectMap(command.getName(), param, method.getMapKey(), rowBounds);
    } else {
      result = sqlSession.selectMap(command.getName(), param, method.getMapKey());
    }
    return result;
  }

  // 返回多行结果 调用sqlSession.selectList方法
  private <E> Object executeForMany(SqlSession sqlSession, Object[] args) {
    List<E> result;
    // param： Employee{id=2, lastName='jane', email='jane@qq.com', gender='1'}
    Object param = method.convertArgsToSqlCommandParam(args);
    if (method.hasRowBounds()) {
      // 如果参数含有rowBounds则调用分页的查询
      RowBounds rowBounds = method.extractRowBounds(args);
      result = sqlSession.selectList(command.getName(), param, rowBounds);
    } else {
      // 没有分页则调用普通查询
      result = sqlSession.selectList(command.getName(), param);
    }
    // issue #510 Collections & arrays support
    if (!method.getReturnType().isAssignableFrom(result.getClass())) {
      if (method.getReturnType().isArray()) {
        return convertToArray(result);
      } else {
        return convertToDeclaredCollection(sqlSession.getConfiguration(), result);
      }
    }
    return result;
  }

  private <T> Cursor<T> executeForCursor(SqlSession sqlSession, Object[] args) {
    Cursor<T> result;
    Object param = method.convertArgsToSqlCommandParam(args);
    if (method.hasRowBounds()) {
      RowBounds rowBounds = method.extractRowBounds(args);
      result = sqlSession.selectCursor(command.getName(), param, rowBounds);
    } else {
      result = sqlSession.selectCursor(command.getName(), param);
    }
    return result;
  }

  private <E> Object convertToDeclaredCollection(Configuration config, List<E> list) {
    Object collection = config.getObjectFactory().create(method.getReturnType());
    MetaObject metaObject = config.newMetaObject(collection);
    metaObject.addAll(list);
    return collection;
  }

  @SuppressWarnings("unchecked")
  private <E> Object convertToArray(List<E> list) {
    Class<?> arrayComponentType = method.getReturnType().getComponentType();
    Object array = Array.newInstance(arrayComponentType, list.size());
    if (arrayComponentType.isPrimitive()) {
      for (int i = 0; i < list.size(); i++) {
        Array.set(array, i, list.get(i));
      }
      return array;
    } else {
      return list.toArray((E[])array);
    }
  }

  // 参数map，静态内部类,更严格的get方法，如果没有相应的key，报错
  public static class ParamMap<V> extends HashMap<String, V> {
    private static final long serialVersionUID = -2212268410512043556L;
    @Override
    public V get(Object key) {
      if (!super.containsKey(key)) {
        throw new BindingException("Parameter '" + key + "' not found. Available parameters are " + keySet());
      }
      return super.get(key);
    }

  }
  // 静态内部类，封装了具体执行的动作
  public static class SqlCommand {
    // xml标签的id   通过它可以找到 MappedStatement    eg：org.apache.goat.chapter100.E.E054.EmployeeMapper.getEmpsByConditionIf
    private final String name;
    // CRUD的具体类型   eg： UNKNOWN, INSERT, UPDATE, DELETE, SELECT, FLUSH
    private final SqlCommandType type;

    public SqlCommand(Configuration configuration, Class<?> mapperInterface, Method method) {
      // 拿到全名 比如 org.mybatis.example.UserMapper.selectByPrimaryKey
      final String methodName = method.getName();
      final Class<?> declaringClass = method.getDeclaringClass();
      // 解析 MappedStatement  // MappedStatement对象,封装一个Mapper接口对应的sql操作
      MappedStatement ms = resolveMappedStatement(mapperInterface, methodName, declaringClass,configuration);
      // 检测当前方法是否有对应的 MappedStatement
      if (ms == null) {
        // 检测当前方法是否有 @Flush 注解
        if (method.getAnnotation(Flush.class) != null) {
          // 设置 name 和 type 遍历
          name = null;
          type = SqlCommandType.FLUSH;
        } else {
          // 若 ms == null 且方法无 @Flush 注解，此时抛出异常。这个异常比较常见，大家应该眼熟吧
          throw new BindingException("Invalid bound statement (not found): " + mapperInterface.getName() + "." + methodName);
        }
      } else {
        // 设置 name 和 type 变量  //这个ms.getId，其实就是我们在mapper.xml配置文件中配置一条sql语句设置的id属性的值
        name = ms.getId();
        // sql的类型（insert、update、delete、select）
        type = ms.getSqlCommandType();
        if (type == SqlCommandType.UNKNOWN) {
          //判断SQL标签类型 未知就抛异常
          throw new BindingException("Unknown execution method for: " + name);
        }
      }
    }

    public String getName() {
      return name;
    }

    public SqlCommandType getType() {
      return type;
    }

    private MappedStatement resolveMappedStatement(Class<?> mapperInterface, String methodName, Class<?> declaringClass, Configuration configuration) {
      // 如果不是这个mapper接口的方法，再去查父类
      String statementId = mapperInterface.getName() + "." + methodName;
      if (configuration.hasStatement(statementId)) {
        return configuration.getMappedStatement(statementId);
      } else if (mapperInterface.equals(declaringClass)) {
        return null;
      }
      // 遍历 mapperInterface 实现的所有接口
      for (Class<?> superInterface : mapperInterface.getInterfaces()) {
        if (declaringClass.isAssignableFrom(superInterface)) {
          MappedStatement ms = resolveMappedStatement(superInterface, methodName,declaringClass, configuration);
          if (ms != null) {
            return ms;
          }
        }
      }
      return null;
    }
  }

  /**
   * 方法签名,封装了接口当中方法的 参数类型 返回值类型 等信息
   */
  public static class MethodSignature {
    // 是否返回多条结果
    private final boolean returnsMany;
    // 返回值是否是MAP
    private final boolean returnsMap;
    // 返回值是否是VOID
    private final boolean returnsVoid;
    private final boolean returnsCursor;
    private final boolean returnsOptional;
    // 返回值类型
    private final Class<?> returnType;
    private final String mapKey;
    // 记下ResultHandler是第几个参数 //resultHandler类型参数的位置
    private final Integer resultHandlerIndex;
    // 以下重复循环2遍调用getUniqueParamIndex，是不是降低效率了
    // 记下RowBounds是第几个参数
    private final Integer rowBoundsIndex; //rowBound类型参数的位置
    private final ParamNameResolver paramNameResolver; //用来存放参数信息

    public MethodSignature(Configuration configuration, Class<?> mapperInterface, Method method) {
      // 通过反射解析方法返回类型
      Type resolvedReturnType = TypeParameterResolver.resolveReturnType(method, mapperInterface);
      if (resolvedReturnType instanceof Class<?>) {
        returnType = (Class<?>) resolvedReturnType;
      } else if (resolvedReturnType instanceof ParameterizedType) {
        returnType = (Class<?>) ((ParameterizedType) resolvedReturnType).getRawType();
      } else {
        returnType = method.getReturnType();
      }
      // 检测返回值类型是否是 void、集合或数组、 Cursor、 Map 等
      returnsVoid = void.class.equals(returnType);
      returnsMany = configuration.getObjectFactory().isCollection(returnType) || returnType.isArray();
      returnsCursor = Cursor.class.equals(returnType);
      returnsOptional = Optional.class.equals(returnType);
      // 解析 @MapKey 注解，获取注解内容
      mapKey = getMapKey(method);
      returnsMap = mapKey != null;
      // 分页参数 // 获取 RowBounds 参数在参数列表中的位置，如果参数列表中包含多个 RowBounds 参数，此方法会抛出异常
      rowBoundsIndex = getUniqueParamIndex(method, RowBounds.class);
      // 获取 ResultHandler 参数在参数列表中的位置  // 自定义ResultHandler
      resultHandlerIndex = getUniqueParamIndex(method, ResultHandler.class);
      /** 解析参数列表
       * sos 该种方式 只有在 接口 fooMapper.deleteById(2) 调用才走这里   传统 sqlSession.selectOne("selectById",2)  方式不走这里
      */
      paramNameResolver = new ParamNameResolver(configuration, method);
    }

    /**
     * 创建SqlSession对象需要传递的参数逻辑
     * args是用户mapper所传递的方法参数列表， 如果方法没有参数，则返回null.
     * 如果方法只包含一个参数并且不包含命名参数， 则返回传递的参数值。
     * 如果包含多个参数或包含命名参数，则返回包含名字和对应值的map对象、
     */
    public Object convertArgsToSqlCommandParam(Object[] args) {
      return paramNameResolver.getNamedParams(args);
    }

    public boolean hasRowBounds() {
      return rowBoundsIndex != null;
    }

    public RowBounds extractRowBounds(Object[] args) {
      return hasRowBounds() ? (RowBounds) args[rowBoundsIndex] : null;
    }

    public boolean hasResultHandler() {
      return resultHandlerIndex != null;
    }

    public ResultHandler extractResultHandler(Object[] args) {
      return hasResultHandler() ? (ResultHandler) args[resultHandlerIndex] : null;
    }

    public String getMapKey() {
      return mapKey;
    }

    public Class<?> getReturnType() {
      return returnType;
    }

    public boolean returnsMany() {
      return returnsMany;
    }

    public boolean returnsMap() {
      return returnsMap;
    }

    public boolean returnsVoid() {
      return returnsVoid;
    }

    public boolean returnsCursor() {
      return returnsCursor;
    }

    /**
     * return whether return type is {@code java.util.Optional}.
     * @return return {@code true}, if return type is {@code java.util.Optional}
     * @since 3.5.0
     */
    public boolean returnsOptional() {
      return returnsOptional;
    }

    private Integer getUniqueParamIndex(Method method, Class<?> paramType) {
      Integer index = null;
      final Class<?>[] argTypes = method.getParameterTypes();
      for (int i = 0; i < argTypes.length; i++) {
        if (paramType.isAssignableFrom(argTypes[i])) {
          if (index == null) {
            index = i;
          } else {
            throw new BindingException(method.getName() + " cannot have multiple " + paramType.getSimpleName() + " parameters");
          }
        }
      }
      return index;
    }

    private String getMapKey(Method method) {
      String mapKey = null;
      if (Map.class.isAssignableFrom(method.getReturnType())) {
        //如果返回类型是map类型的，查看该method是否有MapKey注解。如果有这个注解，将这个注解的值作为map的key
        final MapKey mapKeyAnnotation = method.getAnnotation(MapKey.class);
        if (mapKeyAnnotation != null) {
          mapKey = mapKeyAnnotation.value();
        }
      }
      return mapKey;
    }
  }
}
