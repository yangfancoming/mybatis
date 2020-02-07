#
    A000 mybatis  读取 xml 的三种方式 
    A001 mybatis  无mapper接口 入门示例
    A002 mybatis  有mapper接口 入门示例
# 全局 xml 
    A010 全局xml 配置之 <settings> 之  mapUnderscoreToCamelCase
    A012 全局xml 配置之 <settings> 之  defaultExecutorType
    A020 全局xml 配置之 <properties>  <properties> 标签从外部配置文件读取属性值 
    A022 全局xml 配置之 <properties>  <properties> 标签从外部配置文件和内部标签 同时读取属性值 
    A030 全局xml 配置之 <typeAliases>  方式一   为单个类 起别名
    A032 全局xml 配置之 <typeAliases>  方式二   批量起别名  通过    <package name="org.apache.goat.common"/> 标签
    A034 全局xml 配置之 <typeAliases>  方式三   使用@Alias注解为某个类型指定新的别名
    A036 全局xml 配置之 <environments>  
    A038 全局xml 配置之 <databaseIdProvider>   多数据库 sql 支持
    A040 全局xml 配置之 <mappers>    注册方式一 resource
    A042 全局xml 配置之 <mappers>    注册方式二 class
    A044 全局xml 配置之 <mappers>    注册方式三 package  批量注册 
    A046 全局xml 配置之 <mappers>    注册方式四 url
    A080 全局xml 配置之 综合所有配置 
    
           
    B000 mybatis 的使用方式 
    B040 无   mapper 接口类的方式
    B060 使用 mapper 接口类的方式typeAliases
    B080 使用 mapper 接口类  无 全局xml 方式 
# 局部 xml 
    C000 局部xml 之
    C001 局部xml 之  简单CRUD
    C002 局部xml 之  多参数查询
    C004 局部xml 之  <insert><selectKey> mysql  新增 获取自动增长 主键
    C006 局部xml 之  <insert><selectKey> Oracle 新增 获取序列 主键

    
    C010 局部xml 之  参数处理 之  单个参数 
    C010 局部xml 之  参数处理 之  多个参数
    C010 局部xml 之  参数处理 之  POJO
    C010 局部xml 之  参数处理 之  Map
    C012 局部xml 之  参数处理 之  #{xxx} 和 ${xxx}
    
    C030 局部xml 之  <insert> 
    C040 局部xml 之  <update> 
    C050 局部xml 之  <delete> 
    
    C070 局部xml 之  <parameterMap>  之 调用存储过程
    
    
    
# 局部xml 之 <select> 标签高级查询 之 <resultMap> 
    E001  返回  List 
    E004  返回  Map 
    E010  局部xml  <select>  之 <resultMap>  之 自定义JavaBean的封装
    E014  局部xml  <select>  之 <resultMap>  之 级联查询 级联属性封装结果集
    E018  局部xml  <select>  之 <resultMap>  之 association 定义关联的单个对象的封装规则；
    E020  局部xml  <select>  之 <resultMap>  之 association 分步查询
    E024  局部xml  <select>  之 <resultMap>  之 association 分步查询&延迟加载
    E030  局部xml  <select>  之 <resultMap>  之 collection 定义关联集合封装规则
    E034  局部xml  <select>  之 <resultMap>  之 collection 分步查询&延迟加载 传递单列值
    E038  局部xml  <select>  之 <resultMap>  之 collection 分步查询&传递多列值&fetchType&延迟加载
    E040  局部xml  <select>  之 <resultMap>  之 discriminator鉴别器
    

# 局部xml 之  动态sql
     E050  局部xml  动态SQL 之  <if> 标签
     E051  局部xml  动态SQL 之  <if> 标签
     E052  局部xml  动态SQL 之  <if> 标签
     E054  局部xml  动态SQL 之  <where> 标签
     E056  局部xml  动态SQL 之  <sql> 和 <include> 标签 
     E058  局部xml  动态SQL 之   <choose> <when> <otherwise>
     E060  局部xml  <selectKey> 标签
     E062  局部xml  动态SQL 之   <where> 
     E064  局部xml  动态SQL 之   <set>
     E066  局部xml  动态SQL 之   <trim>  ---- 暂时为空项目
     E070  局部xml  动态SQL 之   <foreach> 用于 sql in 查询
     E072  局部xml  动态SQL 之   <foreach> 用于 sql 批量插入
     E074  局部xml  动态SQL 之   <foreach> 用于 动态 UPDATE
     E076  局部xml  动态SQL 之   <bind> 


#   缓存


#   插件
#   Spring 整合 
#   Springboot 整合 
#   mybatis 扩展  
        扩展---分页
        扩展---批量
        扩展---存储过程
        扩展---自定义处理器
        
        
