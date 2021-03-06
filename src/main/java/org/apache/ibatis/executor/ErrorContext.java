
package org.apache.ibatis.executor;

/**
 懒汉式  单例
*/
public class ErrorContext {

  private static final String LINE_SEPARATOR = System.getProperty("line.separator","\n");
  /**
   只是这里有个有趣的地方是，LOCAL的静态实例变量使用了ThreadLocal修饰，也就是说它属于每个线程各自的数据，而在instance()方法中，先获取本线程的该实例，如果没有就创建该线程独有的ErrorContext。
  */
  private static final ThreadLocal<ErrorContext> LOCAL = new ThreadLocal<>();

  private ErrorContext stored;
  private String resource;
  private String activity;
  private String object;
  private String message;
  private String sql;
  private Throwable cause;

  // 私有化构造函数
  private ErrorContext() { }

  // 第一次使用时才new  懒汉式
  public static ErrorContext instance() {
    ErrorContext context = LOCAL.get();
    if (context == null) {
      context = new ErrorContext();
      LOCAL.set(context);
    }
    return context;
  }

  public ErrorContext store() {
    ErrorContext newContext = new ErrorContext();
    newContext.stored = this;
    LOCAL.set(newContext);
    return LOCAL.get();
  }

  public ErrorContext recall() {
    if (stored != null) {
      LOCAL.set(stored);
      stored = null;
    }
    return LOCAL.get();
  }

  public ErrorContext resource(String resource) {
    this.resource = resource; return this;
  }

  public ErrorContext activity(String activity) {
    this.activity = activity; return this;
  }

  public ErrorContext object(String object) {
    this.object = object;return this;
  }

  public ErrorContext message(String message) {
    this.message = message;return this;
  }

  public ErrorContext sql(String sql) {
    this.sql = sql; return this;
  }

  public ErrorContext cause(Throwable cause) {
    this.cause = cause;return this;
  }

  public ErrorContext reset() {
    resource = null;
    activity = null;
    object = null;
    message = null;
    sql = null;
    cause = null;
    LOCAL.remove();
    return this;
  }

  // -modify
  @Override
  public String toString() {
    StringBuilder description = new StringBuilder();
    if (message != null) {
      commonAppend(description, "### ", message);
    }
    if (resource != null) {
      commonAppend(description, "### The error may exist in ", resource);
    }
    if (object != null) {
      commonAppend(description, "### The error may involve ", object);
    }
    if (activity != null) {
      commonAppend(description, "### The error occurred while ", activity);
    }
    if (sql != null) {
      String trim = sql.replace('\n', ' ').replace('\r', ' ').replace('\t', ' ').trim();
      commonAppend(description, "### SQL: ", trim);
    }
    if (cause != null) {
      commonAppend(description, "### Cause: ", cause.toString());
    }
    return description.toString();
  }

  private void commonAppend(StringBuilder description, String s, String message) {
    description.append(LINE_SEPARATOR);
    description.append(s);
    description.append(message);
  }

}
