MyBatis SQL Mapper Framework for Java
=====================================

[![Build Status](https://travis-ci.org/mybatis/mybatis-3.svg?branch=master)](https://travis-ci.org/mybatis/mybatis-3)
[![Coverage Status](https://coveralls.io/repos/mybatis/mybatis-3/badge.svg?branch=master&service=github)](https://coveralls.io/github/mybatis/mybatis-3?branch=master)
[![Maven central](https://maven-badges.herokuapp.com/maven-central/org.mybatis/mybatis/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.mybatis/mybatis)
[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/https/oss.sonatype.org/org.mybatis/mybatis.svg)](https://oss.sonatype.org/content/repositories/snapshots/org/mybatis/mybatis)
[![License](http://img.shields.io/:license-apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
[![Stack Overflow](http://img.shields.io/:stack%20overflow-mybatis-brightgreen.svg)](http://stackoverflow.com/questions/tagged/mybatis)
[![Project Stats](https://www.openhub.net/p/mybatis/widgets/project_thin_badge.gif)](https://www.openhub.net/p/mybatis)

![mybatis](http://mybatis.github.io/images/mybatis-logo.png)

The MyBatis SQL mapper framework makes it easier to use a relational database with object-oriented applications.
MyBatis couples objects with stored procedures or SQL statements using a XML descriptor or annotations.
Simplicity is the biggest advantage of the MyBatis data mapper over object relational mapping tools.

Essentials
----------

* [See the docs](http://mybatis.github.io/mybatis-3)
* [Download Latest](https://github.com/mybatis/mybatis-3/releases)
* [Download Snapshot](https://oss.sonatype.org/content/repositories/snapshots/org/mybatis/mybatis/)


# 精心挑选要阅读的源码项目；
    
    饮水思源——官方文档，先看文档再看源码；
    下载源码，安装到本地，保证能编译运行；
    从宏观到微观，从整体到细节；
    找到入口，抓主放次，梳理核心流程；
    源码调试，找到核心数据结构和关键类；
    勤练习，多折腾；
    
    
#Mybatis至少遇到了以下的设计模式的使用：

    Builder模式，例如SqlSessionFactoryBuilder、XMLConfigBuilder、XMLMapperBuilder、XMLStatementBuilder、CacheBuilder；
    工厂模式，例如SqlSessionFactory、ObjectFactory、MapperProxyFactory；
    单例模式，例如ErrorContext和LogFactory；
    代理模式，Mybatis实现的核心，比如MapperProxy、ConnectionLogger，用的jdk的动态代理；还有executor.loader包使用了cglib或者javassist达到延迟加载的效果；
    组合模式，例如SqlNode和各个子类ChooseSqlNode等；
    模板方法模式，例如BaseExecutor和SimpleExecutor，还有BaseTypeHandler和所有的子类例如IntegerTypeHandler；
    适配器模式，例如Log的Mybatis接口和它对jdbc、log4j等各种日志框架的适配实现；
    装饰者模式，例如Cache包中的cache.decorators子包中等各个装饰者的实现；
    迭代器模式，例如迭代器模式PropertyTokenizer；
    
# Mapper动态代理开发（重点） 
    底层是通过接口，JDK动态代理，原生MybatisAPI来实现的
    只写接口，实现类由mybatis生成
    四个原则：Mapper接口开发需要遵循以下规范
    1、 Mapper.xml文件中的namespace与mapper接口的类路径相同。
    2、 Mapper接口方法名和Mapper.xml中定义的每个statement的id相同
    3、 Mapper接口方法的输入参数类型和mapper.xml中定义的每个sql 的parameterType的类型相同
    4、 Mapper接口方法的输出参数类型和mapper.xml中定义的每个sql的resultType的类型相同

#学习计划
    1、了解Mybatis运行的流程，对整个框架有整体的把握。
    2、深入工作原理，对详细的参数进行分析。
    3、从各种SQL语句的执行和返回，分析一条SQL语句是如何执行的，最后返回的结果是如何处理的。一条线贯穿。
    4、了解各种类在其中的作用，以及为什么要这样设计，这样设计的好处，以及其中使用的设计模式。
    5、在实际的使用中需要注意的点，以及实际中可以使用的一些技巧。
    6、mybatis与Spring的关系，这里不会特别深入，需要知道一些配置和两者如何进行协同工作的。
    7、如果有时间会自己进行MyBatis的部分功能的实现，模仿源码去构建一些类，实现那些功能。
    8、整理所学的知识，做一个整体的总结。
    
# CustomizedSettingsMapperConfig.xml
    1.properties---------属性
    2.settings-----------全局配置参数
    3.typeAliases--------类型别名
    4.typeHandlers------类型处理器
    5.objectFactory-----对象工厂
    6.plugins------------插件
    7.environments-----环境集合属性对象
        environments ---------环境子属性对象
    　　  transactionManager----------事务管理
           dataSource-------------------数据源
    8.mappers ---------映射器

# MyBatis核心构件
    名称	作用
    SqlSession	          作为MyBatis工作的主要顶层API，表示和数据库交互的会话，完成必要数据库增删改查功能
    Executor	           MyBatis执行器，是MyBatis 调度的核心，负责SQL语句的生成和查询缓存的维护
    StatementHandler	封装了JDBC Statement操作，负责对JDBC statement 的操作，如设置参数、将Statement结果集转换成List集合
    ParameterHandler	负责对用户传递的参数转换成JDBC Statement 所需要的参数
    ResultSetHandler	负责将JDBC返回的ResultSet结果集对象转换成List类型的集合
    TypeHandler 	    负责java数据类型和jdbc数据类型之间的映射和转换
    MappedStatement 	MappedStatement维护了一条select
    SqlSource	        负责根据用户传递的parameterObject，动态地生成SQL语句，将信息封装到BoundSql对象中，并返回
    BoundSql	        表示动态生成的SQL语句以及相应的参数信息
    Configuration   	MyBatis所有的配置信息都维持在Configuration对象之中
    
    
#  Mybatis 解析 mapper 文件的四种方式 ：
       第一种方式 ：（resourc）通过resource指定   <mapper resource="com/dy/dao/userDao.xml"/>
     
       第二种方式， （class ）通过class指定接口，进而将接口与对应的xml文件形成映射关系 不
                    过，使用这种方式必须保证 接口与mapper文件同名(不区分大小写)，  我这儿接口是UserDao,那么意味着mapper文件为UserDao.xml
                    <mapper class="com.dy.dao.UserDao"/>
       
       第三种方式，（package）直接指定包，自动扫描，与方法二同理  <package name="com.dy.dao"/>
    
       第四种方式：（url）通过url指定mapper文件位置 <mapper url="file://........"/>
      
       
