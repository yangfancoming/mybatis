
package org.apache.ibatis.logging;

import java.lang.reflect.Constructor;

/**
 在LogFactory 类加载时会执行其静态代码块，其逻辑是按序加载并实例化对应日志组件的适配器，
 然后使用LogFactory.logConstructor 这个静态字段，记录当前使用的第三方日志组件的适配器，
 自动扫描日志实现，并且第三方日志插件加载优先级如下
 slf4J → commonsLoging → Log4J2 → Log4J → JdkLog
*/
public final class LogFactory {

  /**
   * Marker to be used by logging implementations that support markers.
   *  给支持marker功能的logger使用(目前有slf4j, log4j2)
   */
  public static final String MARKER = "MYBATIS";

  /**
   第三方日志组件构造器，默认为空
   存放绑定的日志框架的构造方法；( 绑定哪个日志框架，就把这个日志框架所对应logger的构造函数放进来)
   这里有个特别的地方，是Log变量的的类型是Constructor<? extends Log>，也就是说该工厂生产的不只是一个产品，而是具有Log公共接口的一系列产品，比如Log4jImpl、Slf4jImpl等很多具体的Log。
  */
  private static Constructor<? extends Log> logConstructor;

  /** 依次执行如下代码，当没有该类会抛 ClassNotFoundException ，然后继续执行
   * 1.静态代码块，用来完成Mybatis和第三方日志框架的绑定过程
   * 2.优先级别是 slf4j > common logging > log4j2 > log4j > jdk logging > 没有日志
   * 3.执行逻辑是：按照优先级别的顺序，依次尝试绑定对应的日志组件，一旦绑定成功，后面的就不会再执行了。
   * 我们看tryImplementation方法，tryImplementation方法首先会判断logConstructor是否为空，为空则尝试绑定，
   * 不为空就什么都不做(不空说明已经绑定成功)。
   * 4.假如第一次进来绑定slf4j，logConstructor肯定为空，那么在useSlf4jLogging方法的逻辑里面就会将slf4j的构造方法放到logConstructor里面去，
   * 后面再执行common logging的绑定流程时发现logConstructor不为空，说明前面已经成功初始化了，就不会执行了；
   * 反过来假如slf4j绑定失败，比如依赖包没有或者版本之类的报错，那么setImplementation抛出异常，在tryImplementation里面捕获到异常之后会直接
   * 忽略，然后就继续尝试绑定common logging,直到成功。这就是绑定的整体流程。
   *
   * 自动扫描日志实现，并且第三方日志插件加载优先级如下：slf4J → commonsLoging → Log4J2 → Log4J → JdkLog
   *
   * 依次去尝试设置LogFactory会用哪一种Log实现去做日志。
   * 第一个成功的会塞入logConstructor，用于后面创建mybatis的通用日志接口Logger
   * 最优先的是slf4j。从上到下一次排列
   * 这里有一个问题是为什么每一种useXXX方法都是同步的呢？他们明明就在同一个static块中被依次调用。
   * 后来仔细一想，是因为这些方法也可以在LogFactory的Class被初始化加载之后，在不用的地方被不同的线程调用。
   * 从而动态改变Log的具体实现者，如果这些方法不是synchronized的话，不同的线程对logConstructor就会出现可见性问题。
   * 类似于单例懒加载双重检查锁。
   */
  static {
    tryImplementation(LogFactory::useSlf4jLogging);
    tryImplementation(LogFactory::useCommonsLogging);
    tryImplementation(LogFactory::useLog4J2Logging);
    tryImplementation(LogFactory::useLog4JLogging);
    tryImplementation(LogFactory::useJdkLogging);
    tryImplementation(LogFactory::useNoLogging);
  }

  //私有化，不可以自己创建Mybatis 的日志工厂，只能在static静态代码块初始化
  private LogFactory() {
    // disable construction
  }

  //举例说明：slf4j
  /**
   * 1.类加载器加载LogFactory 当前类 执行static 静态代码块
   * 加载顺序:slf4J → commonsLoging → Log4J2 → Log4J → JdkLog
   * 2.执行
   * tryImplementation(new Runnable() {
   *   @Override
   *  public void run() {
   *     useSlf4jLogging();
   *  }
   *});

   * 3.先运行useSlf4jLogging();
   * 4.setImplementation(org.apache.ibatis.logging.slf4j.Slf4jImpl.class);
   * 5.执行
   *  Constructor<? extends Log> candidate = implClass.getConstructor(String.class);
   *  Log log = candidate.newInstance(LogFactory.class.getName());
   *  Slf4jImpl.class 获取构造器
   *  candidate.newInstance() 调用 Slf4jImpl构造方法进行日志对象实例化
   * 6.mybatis pom.xml 里面所有关于日志的jar包都会写 <optional>false</optional>，所以 你当前项目依赖mybatis并不会加载相应的日志jar包
   *   当系统里面找不到Slf4j的日志jar包，那么就会报ClassNotFoundException 并且会被tryImplementation 捕获住，并且ignore 不做任何处理
   *   一层一层往下找，最后找到useNoLogging
   */

  /**
   * 对外提供2种获取日志实例的方法，类似于Slf4j的LoggerFactory.getLogger(XXX.class);
   */
  public static Log getLog(Class<?> aClass) {
    return getLog(aClass.getName());
  }

  public static Log getLog(String logger) {
    try {
      return logConstructor.newInstance(logger);
    } catch (Throwable t) {
      throw new LogException("Error creating logger for logger " + logger + ".  Cause: " + t, t);
    }
  }

  /**
   * 1.下面的方法都是类似的，对应于前面绑定几种日志组件的情况，就是把对应的类放到setImplementation方法里面去做
   * 具体的绑定细节，细节的处理流程时一样的。优先级降低
   */
  public static synchronized void useCustomLogging(Class<? extends Log> clazz) {
    setImplementation(clazz);
  }

  public static synchronized void useSlf4jLogging() {
    setImplementation(org.apache.ibatis.logging.slf4j.Slf4jImpl.class);
  }

  public static synchronized void useCommonsLogging() {
    setImplementation(org.apache.ibatis.logging.commons.JakartaCommonsLoggingImpl.class);
  }

  public static synchronized void useLog4JLogging() {
    setImplementation(org.apache.ibatis.logging.log4j.Log4jImpl.class);
  }

  public static synchronized void useLog4J2Logging() {
    setImplementation(org.apache.ibatis.logging.log4j2.Log4j2Impl.class);
  }

  public static synchronized void useJdkLogging() {
    setImplementation(org.apache.ibatis.logging.jdk14.Jdk14LoggingImpl.class);
  }
  //这个好像是测试用的，没看到代码中使用了
  public static synchronized void useStdOutLogging() {
    setImplementation(org.apache.ibatis.logging.stdout.StdOutImpl.class);
  }

  public static synchronized void useNoLogging() {
    setImplementation(org.apache.ibatis.logging.nologging.NoLoggingImpl.class);
  }

  /** 绑定方法；所有尝试绑定的动作都会走这个方法，如果已经有绑定的了，logConstructor就不为null，就不会再尝试绑定了
   这个方法有点迷惑性，因为它使用 Runnable 接口作为参数，
   而 useXxxLOgging() 方法又是同步方法，很容易联想到多线程，
   实际上这里并没有， Runnable 接口不结合 Thread 类使用它就是一个普通的函数接口。
   除去这些就没什么了，不过是调用了 Runnable 的 run() 方法而已。
  */
  private static void tryImplementation(Runnable runnable) {
    /**
     优先级就是在静态代码块中指定的。先加载slf4J，如果成功，则构造器logConstructor不为空，
     那么后续加载的时候发现构造器不为空，后续的第三方组件不再加载。这样就实现了优先级。
     当构造方法不为空才执行方法
     如果为空则调用runnable.run方法，（注意不是start方法）
    */
    if (logConstructor == null) {
      try {
        runnable.run();
      } catch (Throwable t) {
        // ignore
      }
    }
  }

  /**  绑定的细节
   * 根据指定适配器实现类加载相应的日志组件
   * 通过指定的log类来初始化构造方法
   * 这里尝试传入的是实现了ibatis的Log适配器接口的实现类
   */
  private static void setImplementation(Class<? extends Log> implClass) {
    try {
      //1.获取绑定类的构造方法 //获取指定适配器的构造方法  // 获取 Log 实现类的构造方法，它只有一个字符串作为参数
      //这些实现类都有一个string参数的构造器，用于传入logger的名字，不过这一点并不是从语法上限制死的。而是mybatis对每一个实现都提供了这样的constructor
      Constructor<? extends Log> candidate = implClass.getConstructor(String.class);
      //2.通过构造方法创建一个实例赋值给Log，因为采用了适配器模式，传进来的都是适配者，适配者本身是实现了目标接口的，因此进来的类都是Log接口的子类，这是一个多态的写法
      //实例化适配器  创建一个 Log 对象，打印 debug 日志
      //用mybatis的LogFactory的名字创建一个mybatis的log接口，具体的实现由被适配的具体实现决定
      Log log = candidate.newInstance(LogFactory.class.getName());
      //3.这里第2步的赋值只是为了在这里打印日志，打印提示初始化适配器的类型
      //马上就用了，打印的第一句就是，表明用了什么日志适配器实现
      if (log.isDebugEnabled()) {
        log.debug("Logging initialized using '" + implClass + "' adapter.");
      }
      //3.把绑定的日志组件的构造方法放到logConstructor里面，后面就不会再尝试绑定其他的日志组件了
      // 把 candidate 对象设置到 LogFactory 的静态变量 logConstructor，这个静态变量在 getLog() 方法 中被用到
      //Log适配器的构造器缓存下来，用于后面创建其他日志
      logConstructor = candidate;
      //所以这个方法的核心作用就是，抽取我们的真正的日志适配器实现的constructor缓存起来，用于后面创建log
    } catch (Throwable t) {
      //4.抛出的异常会在tryImplementation方法中捕获，捕获之后会尝试绑定下一个日志组件
      //注意这个方法其实会经常抛错的。因为要用底层的实现的前提是引入了这个实现的包。并且能用。
      // 当不成功的时候，通常就是缺了实现类。比如第一个的Slf4j的logg实现，如果没有slff4j的jar，自然就失败了。
      // 所以在外面tryImplementation这个方法会抓住我们的初始化异常。并忽略它。设置失败就算咯~尝试其他的。
      throw new LogException("Error setting Log implementation.  Cause: " + t, t);
    }
  }

}
