#  Mybatis 解析 mapper 文件的四种方式 ：
       第一种方式 ：（resourc）通过resource指定   <mapper resource="com/dy/dao/userDao.xml"/>
     
       第二种方式， （class ）通过class指定接口，进而将接口与对应的xml文件形成映射关系
                      不过，使用这种方式必须保证 接口与mapper文件同名(不区分大小写)，  
                      并且 接口类和xml文件必须在同一个目录下
                     我这儿接口是UserDao,那么意味着mapper文件为UserDao.xml
                    <mapper class="com.dy.dao.UserDao"/>
                    
      				推荐：
      						比较重要的，复杂的Dao接口我们来写sql映射文件
      						不重要的，简单的Dao接口为了开发快速可以使用注解；              
       
       第三种方式，（package）批量注册方式：直接指定包，自动扫描，与方法二同理  <package name="com.dy.dao"/>
    
       第四种方式：（url）通过url指定mapper文件位置 <mapper url="file://........"/>