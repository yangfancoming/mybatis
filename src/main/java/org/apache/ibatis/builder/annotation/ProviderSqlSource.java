
package org.apache.ibatis.builder.annotation;

import org.apache.ibatis.annotations.Lang;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.reflection.ParamNameResolver;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;


public class ProviderSqlSource implements SqlSource {

  private final Configuration configuration;

  private final Class<?> providerType;
  private final LanguageDriver languageDriver;
  private Method providerMethod;
  private String[] providerMethodArgumentNames;
  private Class<?>[] providerMethodParameterTypes;
  private ProviderContext providerContext;
  private Integer providerContextIndex;

  /**
   * @deprecated Please use the {@link #ProviderSqlSource(Configuration, Object, Class, Method)} instead of this.
   */
  @Deprecated
  public ProviderSqlSource(Configuration configuration, Object provider) {
    this(configuration, provider, null, null);
  }

  /**
   * @since 3.4.5
   */
  public ProviderSqlSource(Configuration configuration, Object provider, Class<?> mapperType, Method mapperMethod) {
    String providerMethodName;
    try {
      this.configuration = configuration;
      Lang lang = mapperMethod == null ? null : mapperMethod.getAnnotation(Lang.class);
      this.languageDriver = configuration.getLanguageDriver(lang == null ? null : lang.value());
      this.providerType = getProviderType(provider, mapperMethod);
      providerMethodName = (String) provider.getClass().getMethod("method").invoke(provider);
      if (providerMethodName.length() == 0 && ProviderMethodResolver.class.isAssignableFrom(providerType)) {
        providerMethod = ((ProviderMethodResolver) providerType.getDeclaredConstructor().newInstance()).resolveMethod(new ProviderContext(mapperType, mapperMethod, configuration.getDatabaseId()));
      }
      if (providerMethod == null) {
        providerMethodName = providerMethodName.length() == 0 ? "provideSql" : providerMethodName;
        for (Method m : providerType.getMethods()) {
          if (providerMethodName.equals(m.getName()) && CharSequence.class.isAssignableFrom(m.getReturnType())) {
            if (providerMethod != null) {
              throw new BuilderException("Error creating SqlSource for SqlProvider. Method '" + providerMethodName + "' is found multiple in SqlProvider '" + providerType.getName() + "'. Sql provider method can not overload.");
            }
            providerMethod = m;
          }
        }
      }
    } catch (BuilderException e) {
      throw e;
    } catch (Exception e) {
      throw new BuilderException("Error creating SqlSource for SqlProvider.  Cause: " + e, e);
    }
    if (providerMethod == null) {
      throw new BuilderException("Error creating SqlSource for SqlProvider. Method '" + providerMethodName + "' not found in SqlProvider '" + providerType.getName() + "'.");
    }
    providerMethodArgumentNames = new ParamNameResolver(configuration, providerMethod).getNames();
    providerMethodParameterTypes = providerMethod.getParameterTypes();
    for (int i = 0; i < providerMethodParameterTypes.length; i++) {
      Class<?> parameterType = providerMethodParameterTypes[i];
      if (parameterType == ProviderContext.class) {
        if (providerContext != null) {
          throw new BuilderException("Error creating SqlSource for SqlProvider. ProviderContext found multiple in SqlProvider method ("
              + providerType.getName() + "." + providerMethod.getName() + "). ProviderContext can not define multiple in SqlProvider method argument.");
        }
        providerContext = new ProviderContext(mapperType, mapperMethod, configuration.getDatabaseId());
        providerContextIndex = i;
      }
    }
  }

  @Override
  public BoundSql getBoundSql(Object parameterObject) {
    SqlSource sqlSource = createSqlSource(parameterObject);
    return sqlSource.getBoundSql(parameterObject);
  }

  private SqlSource createSqlSource(Object parameterObject) {
    try {
      int bindParameterCount = providerMethodParameterTypes.length - (providerContext == null ? 0 : 1);
      String sql;
      if (providerMethodParameterTypes.length == 0) {
        sql = invokeProviderMethod();
      } else if (bindParameterCount == 0) {
        sql = invokeProviderMethod(providerContext);
      } else if (bindParameterCount == 1 && (parameterObject == null ||
        providerMethodParameterTypes[providerContextIndex == null || providerContextIndex == 1 ? 0 : 1].isAssignableFrom(parameterObject.getClass()))) {
        sql = invokeProviderMethod(extractProviderMethodArguments(parameterObject));
      } else if (parameterObject instanceof Map) {
        @SuppressWarnings("unchecked")
        Map<String, Object> params = (Map<String, Object>) parameterObject;
        sql = invokeProviderMethod(extractProviderMethodArguments(params, providerMethodArgumentNames));
      } else {
        throw new BuilderException("Error invoking SqlProvider method (" + providerType.getName() + "." + providerMethod.getName()
                + "). Cannot invoke a method that holds " + (bindParameterCount == 1 ? "named argument(@Param)" : "multiple arguments")
                + " using a specifying parameterObject. In this case, please specify a 'java.util.Map' object.");
      }
      Class<?> parameterType = parameterObject == null ? Object.class : parameterObject.getClass();
      return languageDriver.createSqlSource(configuration, sql, parameterType);
    } catch (BuilderException e) {
      throw e;
    } catch (Exception e) {
      throw new BuilderException("Error invoking SqlProvider method (" + providerType.getName() + "." + providerMethod.getName()  + ").  Cause: " + e, e);
    }
  }

  private Object[] extractProviderMethodArguments(Object parameterObject) {
    if (providerContext != null) {
      Object[] args = new Object[2];
      args[providerContextIndex == 0 ? 1 : 0] = parameterObject;
      args[providerContextIndex] = providerContext;
      return args;
    } else {
      return new Object[] { parameterObject };
    }
  }

  private Object[] extractProviderMethodArguments(Map<String, Object> params, String[] argumentNames) {
    Object[] args = new Object[argumentNames.length];
    for (int i = 0; i < args.length; i++) {
      if (providerContextIndex != null && providerContextIndex == i) {
        args[i] = providerContext;
      } else {
        args[i] = params.get(argumentNames[i]);
      }
    }
    return args;
  }

  private String invokeProviderMethod(Object... args) throws Exception {
    Object targetObject = null;
    if (!Modifier.isStatic(providerMethod.getModifiers())) {
      targetObject = providerType.newInstance();
    }
    CharSequence sql = (CharSequence) providerMethod.invoke(targetObject, args);
    return sql != null ? sql.toString() : null;
  }

  private Class<?> getProviderType(Object providerAnnotation, Method mapperMethod) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Class<?> type = (Class<?>) providerAnnotation.getClass().getMethod("type").invoke(providerAnnotation);
    Class<?> value = (Class<?>) providerAnnotation.getClass().getMethod("value").invoke(providerAnnotation);
    if (value == void.class && type == void.class) {
      throw new BuilderException("Please specify either 'value' or 'type' attribute of @"+ ((Annotation) providerAnnotation).annotationType().getSimpleName() + " at the '" + mapperMethod.toString() + "'.");
    }
    if (value != void.class && type != void.class && value != type) {
      throw new BuilderException("Cannot specify different class on 'value' and 'type' attribute of @"+ ((Annotation) providerAnnotation).annotationType().getSimpleName() + " at the '" + mapperMethod.toString() + "'.");
    }
    return value == void.class ? type : value;
  }

}
