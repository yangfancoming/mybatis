
package org.apache.ibatis.submitted.constructor_columnprefix;

import org.apache.ibatis.annotations.Arg;
import org.apache.ibatis.annotations.ConstructorArgs;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface Mapper {

  List<Article> getArticles();

  @ConstructorArgs({
      @Arg(id = true, resultMap = "keyRM", columnPrefix = "key_", javaType = EntityKey.class),
      @Arg(column = "name", javaType = String.class),
      @Arg(resultMap = "authorRM", columnPrefix = "author_", javaType = Author.class),
      @Arg(resultMap = "authorRM", columnPrefix = "coauthor_", javaType = Author.class),
  })
  @Select({
      "select id key_id, name, author.id author_id, author.name author_name,",
      "  coauthor.id coauthor_id, coauthor.name coauthor_name",
      "from articles",
      "left join authors author on author.id = articles.author_id",
      "left join authors coauthor on coauthor.id = articles.coauthor_id",
      "order by articles.id"
  })
  List<Article> getArticlesAnno();

}
