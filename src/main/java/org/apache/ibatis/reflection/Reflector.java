
package org.apache.ibatis.reflection;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.ReflectPermission;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.ibatis.reflection.invoker.GetFieldInvoker;
import org.apache.ibatis.reflection.invoker.Invoker;
import org.apache.ibatis.reflection.invoker.MethodInvoker;
import org.apache.ibatis.reflection.invoker.SetFieldInvoker;
import org.apache.ibatis.reflection.property.PropertyNamer;

/**
 * This class represents a cached set of class definition information that
 * allows for easy mapping between property names and getter/setter methods.
 * 缓存了反射操作需要使用的类的元信息。 允许在属性名和getter/setter方法之间轻松映射
 *
 Reflector 这个类的用途主要是是通过反射获取目标类的 getter 方法及其返回值类型，setter 方法及其参数值类型等元信息。
 并将获取到的元信息缓存到相应的集合中，供后续使用
 1. Reflector 构造方法及成员变量分析
 2. getter 方法解析过程
 3. setter 方法解析过程
 */
public class Reflector {

  // 记录 对应的 class 类型
  private final Class<?> type;

  // 可读属性的名称集合  即存在对应的 getter 方法的属性
  private final String[] readablePropertyNames;

  // 可写属性的名称集合  即存在对应的 setter 方法的属性
  private final String[] writablePropertyNames;

  // 记录了属性相应的 setter 方法  key是属性的名称  value 是 Invoker 对象
  private final Map<String, Invoker> setMethods = new HashMap<>();

  // 记录了属性相应的 getter 方法  key是属性的名称  value 是 Invoker 对象
  private final Map<String, Invoker> getMethods = new HashMap<>();

  // 记录了 相应的 setter 方法参数类型 key是属性名称  value 是参数类型
  private final Map<String, Class<?>> setTypes = new HashMap<>();

  // 记录了 相应的 getter 方法参数类型 key是属性名称  value 是参数类型
  private final Map<String, Class<?>> getTypes = new HashMap<>();

  // 记录了 默认构造方法
  private Constructor<?> defaultConstructor;

  // 记录所有属性名称的集合
  private Map<String, String> caseInsensitivePropertyMap = new HashMap<>();

  /**
   * 构造函数，对上述字段初始化
   */
  public Reflector(Class<?> clazz) {
    type = clazz;
    addDefaultConstructor(clazz); // 解析目标类的默认构造方法，并赋值给 defaultConstructor 变量
    addGetMethods(clazz); // 解析 getter 方法，并将解析结果放入 getMethods 中
    addSetMethods(clazz); // 解析 setter 方法，并将解析结果放入 setMethods 中
    addFields(clazz); // 解析属性字段，并将解析结果添加到 setMethods 或 getMethods 中
    // 从 getMethods 映射中获取可读属性名数组
    readablePropertyNames = getMethods.keySet().toArray(new String[getMethods.keySet().size()]);
    // 从 setMethods 映射中获取可写属性名数组
    writablePropertyNames = setMethods.keySet().toArray(new String[setMethods.keySet().size()]);

    // 将所有属性名的大写形式作为键，属性名作为值，存入到 caseInsensitivePropertyMap 中  ，其中记录了所有大写的属性名称的集合
    for (String propName : readablePropertyNames) {
      caseInsensitivePropertyMap.put(propName.toUpperCase(Locale.ENGLISH), propName);
    }
    for (String propName : writablePropertyNames) {
      caseInsensitivePropertyMap.put(propName.toUpperCase(Locale.ENGLISH), propName);
    }
  }

  /**
   * 获取指定Class对象的 所有的构造函数(public，protected，default(package)access和private)
   *  查找clazz的无参构造方法，通过反射遍历所有构造方法，过滤出构造参数集合长度为0的。
   */
  private void addDefaultConstructor(Class<?> clazz) {
    // 取出所有构造函数 (public，protected，default(package)access和private)
    Constructor<?>[] consts = clazz.getDeclaredConstructors();
    for (Constructor<?> constructor : consts) {
      // 将无参构造函数保存起来 (只有默认构造函数没有参数)
      if (constructor.getParameterTypes().length == 0) {
        this.defaultConstructor = constructor;
      }
    }
  }
  /**   处理clazz 中的getter 方法，填充getMethods 集合和getTypes 集合
   * 负责解析类中定义的getter方法
   1. 获取当前类，接口，以及父类中的方法
   2. 遍历上一步获取的方法数组，并过滤出以 get 和 is 开头的方法
   3. 将方法名转换成相应的属性名
   4. 将属性名和方法对象添加到冲突集合中
   5. 解决冲突
   */
  private void addGetMethods(Class<?> cls) {
    Map<String, List<Method>> conflictingGetters = new HashMap<>();
    // 获取当前类，接口，以及父类中的方法。该方法逻辑不是很复杂，这里就不展开了
    // 获取当前类以及父类中定义的所有方法的唯一签名以及相应的Method对象。
    Method[] methods = getClassMethods(cls);
    for (Method method : methods) {
      // 由于全局配置文件中的<settings>标签的所有属性 对应的Configuration类中的 get 和 is 方法都是没有参数的，若存在参数，则不是<settings>标签对应的属性，必须忽略
      if (method.getParameterTypes().length > 0) {
        continue;
      }
      String name = method.getName();
      // 过滤出方法名是以get开头并且方法名长度大于3 或者 方法名是以is开头并且长度大于2
      if ((name.startsWith("get") && name.length() > 3)|| (name.startsWith("is") && name.length() > 2)) {
        //  将 getXXX 或 isXXX 等方法名转成相应的属性 getMappedStatements ---> mappedStatements
        name = PropertyNamer.methodToProperty(name);
        /*
         * 将冲突的方法添加到 conflictingGetters 中。考虑这样一种情况：
         * getTitle 和 isTitle 两个方法经过 methodToProperty 处理，均得到 name = title，这会导致冲突。
         * 对于冲突的方法，这里先统一起存起来，后续再解决冲突
         */
        addMethodConflict(conflictingGetters, name, method);
      }
    }
    // 解决 getter 冲突
    resolveGetterConflicts(conflictingGetters);
  }

  /**
   *
   * 解决getter方法冲突，寻找最规范和最合理的getter方法
   * 冲突原因：因为在java的继承关系中，会存在子类重写父类的方法，但是放大了返回值类型 所有会存在conflicting冲突的方法
   * 例：
   * super： List<String> getIds();
   * sub: ArrayList<String> getIds();
   * 输入示例：
   * key:  title
   * value:
   *  0 = {Method@1613} "public void org.apache.goat.chapter100.temp.Student.isTitle()"
   *  1 = {Method@1614} "public void org.apache.goat.chapter100.temp.Student.getTitle()"
   *
   由于在获取方法时， 通过调用当前类及其除 Object 之外的所有父类的 getDeclaredMethods 方法及 getInterfaces() 方法，
   因此， 其获取到的方法是该类及其父类的所有方法。
   由此， 产生了一个问题， 如果子类重写了父类中的方法， 如果返回值相同， 则可以通过键重复来去掉。
   但是， 如果方法返回值是父类相同实体方法返回值类型的子类， 则就会导致两个方法是同一个方法， 但是签名不同。
   因此， 需要解决此类冲突。

   内部实现主要是两个for循环，循环比较方法名称相同的情况下，返回值不同的情况下，拿第二个当最终想要的Method。
   <1> 处，基于返回类型比较。重点在 <1.1> 和 <1.2> 的情况，
   因为子类可以修改放大返回值，所以在出现这个情况时，选择子类的该方法。
   例如，父类的一个方法的返回值为 List ，子类对该方法的返回值可以覆写为 ArrayList 。
  */
  private void resolveGetterConflicts(Map<String, List<Method>> conflictingGetters) {
    // 遍历每个属性，查找其最匹配的方法。因为子类可以覆写父类的方法，所以一个属性，可能对应多个 getting 方法
    for (Entry<String, List<Method>> entry : conflictingGetters.entrySet()) {
      // 最匹配的方法
      Method winner = null;
      String propName = entry.getKey();
      for (Method candidate : entry.getValue()) {
        // winner 为空，说明 candidate 为最匹配的方法
        if (winner == null) {
          winner = candidate;
          continue;
        }
        // <1> 基于返回类型比较
        Class<?> winnerType = winner.getReturnType();
        Class<?> candidateType = candidate.getReturnType();
        /*
         * 两个方法的返回值类型一致，若两个方法返回值类型均为 boolean，
         * 则选取 isXXX 方法为 winner。否则无法决定哪个方法更为合适，只能抛出异常
         */
        // 类型相同
        if (candidateType.equals(winnerType)) {
          // 返回值类型相同，应该在 getClassMethods 方法中，已经合并。所以抛出 ReflectionException 异常
          if (!boolean.class.equals(candidateType)) {
            throw new ReflectionException("Illegal overloaded getter method with ambiguous type for property "  + propName + " in class " + winner.getDeclaringClass() + ". This breaks the JavaBeans specification and can cause unpredictable results.");
            // 选择 boolean 类型的 is 方法
          } else if (candidate.getName().startsWith("is")) { // 如果方法返回值类型为 boolean，且方法名以 "is" 开头，则认为候选方法 candidate 更为合适
            winner = candidate;
          }
          // 不符合选择子类
        } else if (candidateType.isAssignableFrom(winnerType)) { // winnerType 是 candidateType 的子类，类型上更为具体,则认为当前的 winner 仍是合适的，无需做什么事情
          // OK getter type is descendant
          // <1.1> 符合选择子类。因为子类可以修改放大返回值。例如，父类的一个方法的返回值为 List ，子类对该方法的返回值可以覆写为 ArrayList
        } else if (winnerType.isAssignableFrom(candidateType)) { // candidateType 是 winnerType 的子类，此时认为 candidate 方法 更为合适， 故将 winner 更新为 candidate
          winner = candidate;
        } else {// <1.2> 返回类型冲突，抛出 ReflectionException 异常
          throw new ReflectionException("Illegal overloaded getter method with ambiguous type for property " + propName + " in class " + winner.getDeclaringClass() + ". This breaks the JavaBeans specification and can cause unpredictable results.");
        }
      }
      // 将筛选出的方法添加到 getMethods 中，并将方法返回值添加到 getTypes 中
      // <2> 添加到 getMethods 和 getTypes 中
      addGetMethod(propName, winner);
    }
  }

  /**
   * 收集get方法
   */
  private void addGetMethod(String name, Method method) {
    //检查属性名是否合法，检查条件方法名不以$开头，不等于serialVersionUID  不等于class
    if (isValidPropertyName(name)) {
      getMethods.put(name, new MethodInvoker(method));
      // 解析返回值类型
      Type returnType = TypeParameterResolver.resolveReturnType(method, type);
      // 将返回值类型由 Type 转为 Class，并将转换后的结果缓存到 setTypes 中
      getTypes.put(name, typeToClass(returnType));
    }
  }
  /**
   * 收集set方法
   */
  private void addSetMethods(Class<?> cls) {
    Map<String, List<Method>> conflictingSetters = new HashMap<>();
    // 获取当前类，接口，以及父类中的方法。该方法逻辑不是很复杂，这里就不展开了
    Method[] methods = getClassMethods(cls);
    for (Method method : methods) {
      String name = method.getName();
      // 过滤出 setter 方法，且方法仅有一个参数
      if (name.startsWith("set") && name.length() > 3) {
        if (method.getParameterTypes().length == 1) {
          name = PropertyNamer.methodToProperty(name);
          // setter 方法发生冲突原因是：可能存在重载情况，比如： void setSex(int sex);   void setSex(SexEnum sex);
          addMethodConflict(conflictingSetters, name, method);
        }
      }
    }
    resolveSetterConflicts(conflictingSetters);
  }

  /** 添加属性名和方法对象到冲突集合中 */
  private void addMethodConflict(Map<String, List<Method>> conflictingMethods, String name, Method method) {
    List<Method> list = conflictingMethods.computeIfAbsent(name, k -> new ArrayList<>());
    // 这里需要注意：add 操作的是已经put到 conflictingMethods Map集合中的list 并不是多余操作
    list.add(method);
  }
  /** 解决冲突
   * 关于 setter 方法冲突的解析规则，这里也总结一下吧。如下：
   * 1. 冲突方法的参数类型与 getter 的返回类型一致，则认为是最好的选择
   * 2. 冲突方法的参数类型具有继承关系，子类参数对应的方法被认为是更合适的选择
   * 3. 冲突方法的参数类型不相关，无法确定哪个是更好的选择，此时直接抛异常
   * */
  private void resolveSetterConflicts(Map<String, List<Method>> conflictingSetters) {
    for (String propName : conflictingSetters.keySet()) {
      List<Method> setters = conflictingSetters.get(propName);
      //  获取 getter 方法的返回值类型，由于 getter 方法不存在重载的情况，所以可以用它的返回值类型反推哪个 setter 的更为合适
      Class<?> getterType = getTypes.get(propName);
      Method match = null;
      ReflectionException exception = null;
      for (Method setter : setters) {
        // 获取参数类型
        Class<?> paramType = setter.getParameterTypes()[0];
        if (paramType.equals(getterType)) {
          // should be the best match 参数类型和返回类型一致，则认为是最好的选择，并结束循环
          match = setter;
          break;
        }
        if (exception == null) {
          try {
            match = pickBetterSetter(match, setter, propName); // 选择一个更为合适的方法
          } catch (ReflectionException e) {
            // there could still be the 'best match'
            match = null;
            exception = e;
          }
        }
      }
      if (match == null) { // 若 match 为空，表示没找到更为合适的方法，此时抛出异常
        throw exception;
      } else {
        addSetMethod(propName, match);// 将筛选出的方法放入 setMethods 中，并将方法参数值添加到 setTypes 中
      }
    }
  }

  /** 从两个 setter 方法中选择一个更为合适方法 */
  private Method pickBetterSetter(Method setter1, Method setter2, String property) {
    if (setter1 == null) {
      return setter2;
    }
    Class<?> paramType1 = setter1.getParameterTypes()[0];
    Class<?> paramType2 = setter2.getParameterTypes()[0];
    // 如果参数 2 可赋值给参数 1，即参数 2 是参数 1 的子类，则认为参数 2 对应的 setter 方法更为合适
    if (paramType1.isAssignableFrom(paramType2)) {
      return setter2;
    } else if (paramType2.isAssignableFrom(paramType1)) {// 这里和上面情况相反
      return setter1;
    }
    // 两种参数类型不相关，这里抛出异常
    throw new ReflectionException("Ambiguous setters defined for property '" + property + "' in class '" + setter2.getDeclaringClass() + "' with types '" + paramType1.getName() + "' and '" + paramType2.getName() + "'.");
  }

  private void addSetMethod(String name, Method method) {
    if (isValidPropertyName(name)) {
      setMethods.put(name, new MethodInvoker(method));
      // 解析参数类型列表
      Type[] paramTypes = TypeParameterResolver.resolveParamTypes(method, type);
      // 将参数类型由 Type 转为 Class，并将转换后的结果缓存到 setTypes
      setTypes.put(name, typeToClass(paramTypes[0]));
    }
  }

  private Class<?> typeToClass(Type src) {
    Class<?> result = null;
    if (src instanceof Class) {
      result = (Class<?>) src;
    } else if (src instanceof ParameterizedType) {
      result = (Class<?>) ((ParameterizedType) src).getRawType();
    } else if (src instanceof GenericArrayType) {
      Type componentType = ((GenericArrayType) src).getGenericComponentType();
      if (componentType instanceof Class) {
        result = Array.newInstance((Class<?>) componentType, 0).getClass();
      } else {
        Class<?> componentClass = typeToClass(componentType);
        result = Array.newInstance(componentClass, 0).getClass();
      }
    }
    if (result == null) {
      result = Object.class;
    }
    return result;
  }

  private void addFields(Class<?> clazz) {
    Field[] fields = clazz.getDeclaredFields();
    for (Field field : fields) {
      if (!setMethods.containsKey(field.getName())) {
        // issue #379 - removed the check for final because JDK 1.5 allows
        // modification of final fields through reflection (JSR-133). (JGB)
        // pr #16 - final static can only be set by the classloader
        int modifiers = field.getModifiers();
        if (!(Modifier.isFinal(modifiers) && Modifier.isStatic(modifiers))) {
          addSetField(field);
        }
      }
      if (!getMethods.containsKey(field.getName())) {
        addGetField(field);
      }
    }
    if (clazz.getSuperclass() != null) {
      addFields(clazz.getSuperclass());
    }
  }

  private void addSetField(Field field) {
    if (isValidPropertyName(field.getName())) {
      setMethods.put(field.getName(), new SetFieldInvoker(field));
      Type fieldType = TypeParameterResolver.resolveFieldType(field, type);
      setTypes.put(field.getName(), typeToClass(fieldType));
    }
  }

  private void addGetField(Field field) {
    if (isValidPropertyName(field.getName())) {
      getMethods.put(field.getName(), new GetFieldInvoker(field));
      Type fieldType = TypeParameterResolver.resolveFieldType(field, type);
      getTypes.put(field.getName(), typeToClass(fieldType));
    }
  }

  private boolean isValidPropertyName(String name) {
    return !(name.startsWith("$") || "serialVersionUID".equals(name) || "class".equals(name));
  }

  /**
   * This method returns an array containing all methods declared in this class and any superclass.
   * We use this method, instead of the simpler <code>Class.getMethods()</code>,
   * because we want to look for private methods as well.
   * 此方法返回一个数组，该数组包含该类中声明的所有方法和任何超类
   * 我们使用此方法不是为了代替 Class.getMethods(),因为我们想访问类中的私有方法.
   * @param cls The class
   * @return An array containing all methods in this class 包含该类中所有方法的数组
   * //获取当前类以及父类中定义的所有方法的唯一签名以及相应的Method对象。
   */
  private Method[] getClassMethods(Class<?> cls) {
    //用于记录指定类中定义的全部方法的唯一签名以及对应的Method对象
    Map<String, Method> uniqueMethods = new HashMap<>();
    Class<?> currentClass = cls;
    while (currentClass != null && currentClass != Object.class) {
      //currentClass.getDeclaredMethods(),获取当前类中定义的所有方法
      //addUniqueMethods 为每个方法生成唯一签名，并记录到uniqueMethods集合中
      addUniqueMethods(uniqueMethods, currentClass.getDeclaredMethods());
      // we also need to look for interface methods - because the class may be abstract
      // 获取当前类对象 实现的所有接口
      Class<?>[] interfaces = currentClass.getInterfaces();
      for (Class<?> anInterface : interfaces) {
        //addUniqueMethods 为每个接口中的每个方法都生成唯一签名，并记录到uniqueMethods集合中
        addUniqueMethods(uniqueMethods, anInterface.getMethods());
      }
      // 当前类的父类，继续while循环  逐层递增直到遍历到Object对象
      currentClass = currentClass.getSuperclass();
    }
    Collection<Method> methods = uniqueMethods.values();
    //转换成数组返回
    return methods.toArray(new Method[methods.size()]);
  }

  /**
   * 为每个方法生成唯一签名，并记录到uniqueMethods集合中
   */
  private void addUniqueMethods(Map<String, Method> uniqueMethods, Method[] methods) {
    for (Method currentMethod : methods) {
      if (!currentMethod.isBridge()) {
        //得到方法签名 格式为：方法返回参数#方法名:参数名 ps：多个参数用,分割 签名样例:String#getName:User
        String signature = getSignature(currentMethod);
        //如果签名存在，则不做处理，表示子类已经覆盖了该方法。
        //如果签名不存在，则将签名作为Key,Method作为value 添加到uniqueMethods中
        if (!uniqueMethods.containsKey(signature)) {
          uniqueMethods.put(signature, currentMethod);
        }
      }
    }
  }

  /**
   *  给指定方法生成唯一签名  生成签名的规则是： 方法返回值#方法名#参数名
   * @param method  待生成唯一签名的方法
   * 输入示例：  Method eat = Human.class.getMethod("eat");
   * 输出结果：  Assert.assertEquals("void#eat", ReflectUtil.getSignature(eat));
   *
   * 输入示例：  Method sing = Human.class.getMethod("sing",String.class);
   * 输出结果：  Assert.assertEquals("void#sing:java.lang.String", ReflectUtil.getSignature(sing));
   *
   * 输入示例：  Method study = Human.class.getMethod("study",String.class,Integer.class);
   * 输出结果：  Assert.assertEquals("java.lang.String#study:java.lang.String,java.lang.Integer", ReflectUtil.getSignature(study));
   */
  private String getSignature(Method method) {
    StringBuilder sb = new StringBuilder();
    Class<?> returnType = method.getReturnType();
    if (returnType != null) {
      sb.append(returnType.getName()).append('#');
    }
    sb.append(method.getName());
    Class<?>[] parameters = method.getParameterTypes();
    for (int i = 0; i < parameters.length; i++) {
      sb.append((i == 0)? ':':','); // modify-
      sb.append(parameters[i].getName());
    }
    return sb.toString();
  }

  /**
   * Checks whether can control member accessible.
   * 检查是否拥有了访问的权限：除了访问公有的变量， 还能访问 default , protected 和private 变量
   * @return If can control member accessible, it return {@literal true}
   * @since 3.5.0
   */
  public static boolean canControlMemberAccessible() {
    try {
      SecurityManager securityManager = System.getSecurityManager();
      if (null != securityManager) {
        securityManager.checkPermission(new ReflectPermission("suppressAccessChecks"));
      }
    } catch (SecurityException e) {
      return false;
    }
    return true;
  }

  /**
   * Gets the name of the class the instance provides information for.
   * @return The class name
   */
  public Class<?> getType() {
    return type;
  }

  public Constructor<?> getDefaultConstructor() {
    if (defaultConstructor != null) {
      return defaultConstructor;
    } else {
      throw new ReflectionException("There is no default constructor for " + type);
    }
  }

  public boolean hasDefaultConstructor() {
    return defaultConstructor != null;
  }

  // 获取所有可读属性的 Invoker
  public Invoker getSetInvoker(String propertyName) {
    Invoker method = setMethods.get(propertyName);
    if (method == null) {
      throw new ReflectionException("There is no setter for property named '" + propertyName + "' in '" + type + "'");
    }
    return method;
  }

  // 获取所有可读属性的 Invoker
  public Invoker getGetInvoker(String propertyName) {
    Invoker method = getMethods.get(propertyName);
    if (method == null) {
      throw new ReflectionException("There is no getter for property named '" + propertyName + "' in '" + type + "'");
    }
    return method;
  }

  /**
   * Gets the type for a property setter.
   * // 获取对应属性的类型
   * @param propertyName - the name of the property
   * @return The Class of the property setter
   */
  public Class<?> getSetterType(String propertyName) {
    Class<?> clazz = setTypes.get(propertyName);
    if (clazz == null) {
      throw new ReflectionException("There is no setter for property named '" + propertyName + "' in '" + type + "'");
    }
    return clazz;
  }

  /**
   * Gets the type for a property getter.
   *  获取对应属性的类型
   * @param propertyName - the name of the property
   * @return The Class of the property getter
   */
  public Class<?> getGetterType(String propertyName) {
    Class<?> clazz = getTypes.get(propertyName);
    if (clazz == null) {
      throw new ReflectionException("There is no getter for property named '" + propertyName + "' in '" + type + "'");
    }
    return clazz;
  }

  /**
   * Gets an array of the readable properties for an object.
   *  获取所有的可读属性
   * @return The array
   */
  public String[] getGetablePropertyNames() {
    return readablePropertyNames;
  }

  /**
   * Gets an array of the writable properties for an object.
   *  // 获取所有的可读属性
   * @return The array
   */
  public String[] getSetablePropertyNames() {
    return writablePropertyNames;
  }

  /**
   * Check to see if a class has a writable property by name.
   * @param propertyName - the name of the property to check
   * @return True if the object has a writable property by the name
   */
  public boolean hasSetter(String propertyName) {
    return setMethods.keySet().contains(propertyName);
  }

  /**
   * Check to see if a class has a readable property by name.
   * 对应属性是否有相应的getter
   * @param propertyName - the name of the property to check
   * @return True if the object has a readable property by the name
   */
  public boolean hasGetter(String propertyName) {
    return getMethods.keySet().contains(propertyName);
  }

//  查找是否有相应的属性
  public String findPropertyName(String name) {
    return caseInsensitivePropertyMap.get(name.toUpperCase(Locale.ENGLISH));
  }
}
