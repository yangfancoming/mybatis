
package org.apache.ibatis.submitted.cache;

import java.util.List;

import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Options.FlushCachePolicy;
import org.apache.ibatis.annotations.Select;

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
  void delete(int id);

  @Select("select id, firstname, lastname from person")
  List<Person> findAll();

  @Select("select id, firstname, lastname from person")
  @Options(flushCache = FlushCachePolicy.TRUE)
  List<Person> findWithFlushCache();
}
