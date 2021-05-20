
package org.apache.ibatis.mapping;

import org.apache.ibatis.session.Configuration;

import java.util.Collections;
import java.util.List;

/**
 * 一个ParameterMap对应一个SQL语句中所有的参数，就行ParameterMapping的集合
 *
 *  <parameterMap id="" type=""  >
 *         <parameter property="" resultMap="" javaType="" typeHandler="" jdbcType="" mode="" scale=""></parameter>
 *         <parameter property="" resultMap="" javaType="" typeHandler="" jdbcType="" mode="" scale=""></parameter>
 *         <parameter property="" resultMap="" javaType="" typeHandler="" jdbcType="" mode="" scale=""></parameter>
 *     </parameterMap>
*/
public class ParameterMap {

  private String id;
  private Class<?> type;
  private List<ParameterMapping> parameterMappings;

  private ParameterMap() {
  }

  public static class Builder {
    private ParameterMap parameterMap = new ParameterMap();

    public Builder(Configuration configuration, String id, Class<?> type, List<ParameterMapping> parameterMappings) {
      parameterMap.id = id;
      parameterMap.type = type;
      parameterMap.parameterMappings = parameterMappings;
    }

    public Class<?> type() {
      return parameterMap.type;
    }

    public ParameterMap build() {
      //lock down collections
      parameterMap.parameterMappings = Collections.unmodifiableList(parameterMap.parameterMappings);
      return parameterMap;
    }
  }

  public String getId() {
    return id;
  }

  public Class<?> getType() {
    return type;
  }

  public List<ParameterMapping> getParameterMappings() {
    return parameterMappings;
  }

}
