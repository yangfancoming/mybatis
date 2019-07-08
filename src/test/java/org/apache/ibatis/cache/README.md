#  mybatis 缓存模块  装饰模式   详情参见 springboot 951 com.goat.B.B02.item04
     缓存框架按照 Key-Value方式存储，Key的生成采取规则为：[hashcode:checksum:mappedStementId:offset:limit:executeSql:queryParams]。
     
     Cache接口定义了缓存接口。
    
     CacheKey定义了缓存的Key。
     PerpetualCache直接实现了Cache接口。
     
     FifoCache,LoggingCache,LruCache,ScheduledCache,SerializedCache,SoftCache,SynchronizedCache,
     TransactionalCache,WeakCache 采用装饰模式实现Cache接口。
     
     采用装饰模式，一个个包装起来，形成一个链，典型的就是SynchronizedCache->LoggingCache->SerializedCache->LruCache->PerpetualCache，通过链起来达到功能增加。
     
     CacheException定义了缓存异常。