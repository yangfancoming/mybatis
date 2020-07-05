
package org.apache.ibatis.session;


/**
 ResultHandler，顾名思义，对返回的结果进行处理，最终得到自己想要的数据格式或类型。也就是说，可以自定义返回类型
*/
public interface ResultHandler<T> {

  void handleResult(ResultContext<? extends T> resultContext);
}
