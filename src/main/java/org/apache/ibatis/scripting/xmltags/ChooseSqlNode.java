
package org.apache.ibatis.scripting.xmltags;

import java.util.List;

/**
 * ChooseSqlNode，如果在编写动态SQL语句时需要类似Java中的switch语句的功能，可以使用<choose>、<when>、<otherwise>三个标签的组合
 *
 *<select id="findList" parameterType="java.util.Map" resultType="com.cloud.model.user.AppUser">
 *    SELECT a.id,a.username,a.password,a.nickname,a.headImgUrl,a.phone,a.sex,a.enabled,a.type,a.createTime,a.updateTime,a.banlance,a.control,a.win,a.loginTime
 *    FROM app_user a
 *    <if test="groupId != null">
 *       <choose>
 *          <when test="groupId != 0">
 *             INNER JOIN user_group b ON a.id=b.user_id AND b.group_id=#{groupId}
 *          </when>
 *          <otherwise>
 *             LEFT JOIN user_group b ON a.id=b.user_id
 *          </otherwise>
 *       </choose>
 *    </if>
 * </select>
*/
public class ChooseSqlNode implements SqlNode {

  //<otherwise>节点对应的SqlNode
  private final SqlNode defaultSqlNode;
  //<when>节点对应的IfSqlNode集合
  private final List<SqlNode> ifSqlNodes;

  public ChooseSqlNode(List<SqlNode> ifSqlNodes, SqlNode defaultSqlNode) {
    this.ifSqlNodes = ifSqlNodes;
    this.defaultSqlNode = defaultSqlNode;
  }

  @Override
  public boolean apply(DynamicContext context) {
    //遍历IfSqlNodes集合，并调用其中SqlNode对象的apply()方法
    for (SqlNode sqlNode : ifSqlNodes) {
      if (sqlNode.apply(context)) {
        return true;
      }
    }
    //调用defaultSqlNode.apply()方法
    if (defaultSqlNode != null) {
      defaultSqlNode.apply(context);
      return true;
    }
    return false;
  }
}
