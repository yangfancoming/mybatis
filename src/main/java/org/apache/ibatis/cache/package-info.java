
/**
 * Base package for caching stuff
 */
package org.apache.ibatis.cache;


/**
 用于装饰PerpetualCache的标准装饰器共有8个（全部在 org.apache.ibatis.cache.decorators包中）：
 1. FifoCache：先进先出算法，缓存回收策略
 2. LoggingCache：输出缓存命中的日志信息
 3. LruCache：最近最少使用算法，缓存回收策略
 4. ScheduledCache：调度缓存，负责定时清空缓存
 5. SerializedCache：缓存序列化和反序列化存储
 6. SoftCache：基于软引用实现的缓存管理策略
 7. SynchronizedCache：同步的缓存装饰器，用于防止多线程并发访问
 8. WeakCache：基于弱引用实现的缓存管理策略


 一级缓存，又叫本地缓存，是PerpetualCache类型的永久缓存，保存在执行器中
 (BaseExecutor)，而执行器又在SqlSession(DefaultSqlSession)中，所以
 一级缓存的生命周期与SqlSession是相同的。
 二级缓存，又叫自定义缓存，实现了Cache接口的类都可以作为二级缓存，所以可配
 置如encache等的第三方缓存。二级缓存以namespace名称空间为其唯一标识，被保
 存在Configuration核心配置对象中
*/
