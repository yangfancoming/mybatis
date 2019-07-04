
package org.apache.ibatis.submitted.not_null_column;

public interface FatherMapper {
  Father selectByIdNoFid(Integer id);

  Father selectByIdFid(Integer id);

  Father selectByIdWithInternalResultMap(Integer id);

  Father selectByIdWithRefResultMap(Integer id);

  Father selectByIdFidMultipleNullColumns(Integer id);

  Father selectByIdFidMultipleNullColumnsAndBrackets(Integer id);

  Father selectByIdFidWorkaround(Integer id);
}
