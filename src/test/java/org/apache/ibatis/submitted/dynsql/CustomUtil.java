
package org.apache.ibatis.submitted.dynsql;

public class CustomUtil {
    public static String esc(final String s) {
        return s.replace("'", "''");
    }
}
