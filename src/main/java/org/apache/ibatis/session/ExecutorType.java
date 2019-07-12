
package org.apache.ibatis.session;


/**
 在mybatis中ExecutorType的使用:

 1.Mybatis内置的ExecutorType有3种，默认的是simple，该模式下它为每个语句的执行创建一个新的预处理语句，单条提交sql；而batch模式重复使用已经预处理的语句，
   并且批量执行所有更新语句，显然batch性能将更优；

 2.但batch模式也有自己的问题，比如在Insert操作时，在事务没有提交之前，是没有办法获取到自增的id，这在某型情形下是不符合业务要求的；

 3.在测试中使用simple模式提交10000条数据，时间为18248 毫秒，batch模式为5023 ，性能提高70%；

*/
public enum ExecutorType {
  SIMPLE, REUSE, BATCH
}
