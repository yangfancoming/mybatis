# 目录
#    第1章 MyBatis入门 1
    
    1.1 MyBatis简介 2
    1.2 创建Maven项目 3
    1.3 简单配置让MyBatis跑起来 7
        1.3.1 准备数据库 8
        1.3.2 配置MyBatis 8
        1.3.3 创建实体类和Mapper.xml文件 10
        1.3.4 配置Log4j以便查看MyBatis操作数据库的过程 11
        1.3.5 编写测试代码让MyBatis跑起来 12
    1.4 本章小结 14
    
#     第2章 MyBatis XML方式的基本用法 15
    
    2.1 一个简单的权限控制需求 16
        2.1.1 创建数据库表 16
        2.1.2 创建实体类 19
    2.2 使用XML方式 21
    2.3 select用法 23
    2.4 insert用法 35
        2.4.1 简单的insert方法 35
        2.4.2 使用JDBC方式返回主键自增的值 38
        2.4.3 使用selectKey返回主键的值 40
    2.5 update用法 42
    2.6 delete用法 45
    2.7 多个接口参数的用法 47
    2.8 Mapper接口动态代理实现原理 50
    2.9 本章小结 51
    
#     第3章 MyBatis注解方式的基本用法 53
    3.1 @Select注解 54
        3.1.1 使用mapUnderscoreToCamelCase配置 55
        3.1.2 使用resultMap方式 55
    3.2 @Insert注解 58
        3.2.1 不需要返回主键 58
        3.2.2 返回自增主键 58
        3.2.3 返回非自增主键 59
    3.3 @Update注解和@Delete注解 59
    3.4 Provider注解 60
    3.5 本章小结 61
    
#     第4章 MyBatis动态SQL 63
    
    4.1 if用法 64
        4.1.1 在WHERE条件中使用if 64
        4.1.2 在UPDATE更新列中使用if 68
        4.1.3 在INSERT动态插入列中使用if 70
    4.2 choose用法 72
    4.3 where、set、trim用法 75
        4.3.1 where用法 75
        4.3.2 set用法 76
        4.3.3 trim用法 77
    4.4 foreach用法 78
        4.4.1 foreach实现in集合 78
        4.4.2 foreach实现批量插入 81
        4.4.3 foreach实现动态UPDATE 84
    4.5 bind用法 86
    4.6 多数据库支持 86
    4.7 OGNL用法 89
    4.8 本章小结 90
    
#     第5章 Mybatis代码生成器 91
    
    5.1 XML配置详解 92
        5.1.1 property标签 95
        5.1.2 plugin标签 96
        5.1.3 commentGenerator标签 97
        5.1.4 jdbcConnection标签 99
        5.1.5 javaTypeResolver标签 100
        5.1.6 javaModelGenerator标签 101
        5.1.7 sqlMapGenerator标签 102
        5.1.8 javaClientGenerator标签 103
        5.1.9 table标签 104
    5.2 一个配置参考示例 109
    5.3 运行MyBatis Generator 111
        5.3.1 使用Java编写代码运行 111
        5.3.2 从命令提示符运行 113
        5.3.3 使用Maven Plugin运行 115
        5.3.4 使用Eclipse插件运行 117
    5.4 Example介绍 119
    5.5 本章小结 124
    
#     第6章 MyBatis高级查询 125
    
    6.1 高级结果映射 126
        6.1.1 一对一映射 126
        6.1.2 一对多映射 140
        6.1.3 鉴别器映射 156
    6.2 存储过程 159
        6.2.1 第一个存储过程 162
        6.2.2 第二个存储过程 164
        6.2.3 第三个和第四个存储过程 166
        6.2.4 在Oracle中使用游标参数的存储过程 168
    6.3 使用枚举或其他对象 170
        6.3.1 使用MyBatis提供的枚举处理器 170
        6.3.2 使用自定义的类型处理器 172
        6.3.3 对Java 8日期（JSR-310）的支持 175
    6.4 本章小结 176
    
#    第7章 MyBatis缓存配置 177
    
    7.1 一级缓存 178
    7.2 二级缓存 181
        7.2.1 配置二级缓存 181
        7.2.2 使用二级缓存 184
    7.3 集成EhCache缓存 187
    7.4 集成Redis缓存 190
    7.5 脏数据的产生和避免 191
    7.6 二级缓存适用场景 194
    7.7 本章小结 194
    
#    第8章 MyBatis插件开发 195
    
    8.1 拦截器接口介绍 196
    8.2 拦截器签名介绍 198
        8.2.1 Executor接口 198
        8.2.2 ParameterHandler接口 200
        8.2.3 ResultSetHandler接口 201
        8.2.4 StatementHandler接口 202
    8.3 下画线键值转小写驼峰形式插件 203
    8.4 分页插件 206
        8.4.1 PageInterceptor拦截器类 207
        8.4.2 Dialect接口 212
        8.4.3 MySqlDialect实现 216
    8.5 本章小结 220
    
#    第9章 Spring集成MyBatis 221
    
    9.1 创建基本的Maven Web项目 222
    9.2 集成Spring和Spring MVC 227
    9.3 集成MyBatis 232
    9.4 几个简单实例 234
        9.4.1 基本准备 235
        9.4.2 开发Mapper层（Dao层） 235
        9.4.3 开发业务层（Service层） 238
        9.4.4 开发控制层（Controller层） 240
        9.4.5 开发视图层（View层） 242
        9.4.6 部署和运行应用 245
    9.5 本章小结 246
    
# 第10章 Spring Boot集成MyBatis 247
    10.1 基本的Spring Boot项目 248
    10.2 集成MyBatis 251
    10.3 MyBatis Starter配置介绍 253
    10.4 简单示例 255
        10.4.1 引入simple依赖 255
        10.4.2 开发业务（Service）层 258
        10.4.3 开发控制（Controller）层 259
        10.4.4 运行应用查看效果 259
    10.5 本章小结 260
    
# 第11章 MyBatis开源项目 261
    11.1 Git入门 262
    11.1.1 初次运行配置 262
    11.1.2 初始化和克隆仓库 263
    11.1.3 本地操作 265
    11.1.4 远程操作 267
    11.2 GitHub入门 269
    11.2.1 创建并提交到仓库 269
    11.2.2 Fork仓库并克隆到本地 272
    11.2.3 社交功能 275
    11.3 MyBatis源码讲解 278
    11.4 MyBatis测试用例 290
    11.5 本章小结 293