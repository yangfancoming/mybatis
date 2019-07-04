
package org.apache.ibatis.submitted.disallowdotsonnames;

import java.io.IOException;
import java.io.Reader;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DisallowDotsOnNamesTest {

    @Test
    void testShouldNotAllowMappedStatementsWithDots() throws IOException {
        Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/disallowdotsonnames/ibatisConfig.xml");
        Assertions.assertThrows(PersistenceException.class, () -> new SqlSessionFactoryBuilder().build(reader));
    }

}
