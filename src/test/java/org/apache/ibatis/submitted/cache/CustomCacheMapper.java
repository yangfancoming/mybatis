
package org.apache.ibatis.submitted.cache;

import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Property;

@CacheNamespace(implementation = CustomCache.class, properties = {
    @Property(name = "stringValue", value = "bar"),
    @Property(name = "integerValue", value = "1"),
    @Property(name = "intValue", value = "2"),
    @Property(name = "longWrapperValue", value = "3"),
    @Property(name = "longValue", value = "4"),
    @Property(name = "shortWrapperValue", value = "5"),
    @Property(name = "shortValue", value = "6"),
    @Property(name = "floatWrapperValue", value = "7.1"),
    @Property(name = "floatValue", value = "8.1"),
    @Property(name = "doubleWrapperValue", value = "9.01"),
    @Property(name = "doubleValue", value = "10.01"),
    @Property(name = "byteWrapperValue", value = "11"),
    @Property(name = "byteValue", value = "12"),
    @Property(name = "booleanWrapperValue", value = "true"),
    @Property(name = "booleanValue", value = "true")
})
public interface CustomCacheMapper {
}
