
package org.apache.ibatis.submitted.cache;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Options.FlushCachePolicy;

import java.util.List;

/**
 <cache/> 与 @CacheNamespace 是一样的
 <cache/> 用在 xml 配置文件中
 @CacheNamespace 用在 mapper接口类中
*/
@CacheNamespace
public interface PersonMapper {

  @Insert("insert into person (id, firstname, lastname) values (#{id}, #{firstname}, #{lastname})")
  void create(Person person);

  @Insert("insert into person (id, firstname, lastname) values (#{id}, #{firstname}, #{lastname})")
  @Options
  void createWithOptions(Person person);

  @Insert("insert into person (id, firstname, lastname) values (#{id}, #{firstname}, #{lastname})")
  @Options(flushCache = FlushCachePolicy.FALSE)
  void createWithoutFlushCache(Person person);

  @Delete("delete from person where id = #{id}")
  void deleteById(int id);

  @Select("select id, firstname, lastname from person")
  List<Person> findAll();

  @Select("select id, firstname, lastname from person")
  @Options(flushCache = FlushCachePolicy.TRUE)
  List<Person> findWithFlushCache();
}
