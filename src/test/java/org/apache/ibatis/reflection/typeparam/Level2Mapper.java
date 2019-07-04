
package org.apache.ibatis.reflection.typeparam;

import java.io.Serializable;
import java.util.Date;

public interface Level2Mapper extends Level1Mapper<Date, Integer>, Serializable, Comparable<Integer> {
}
