
package org.apache.ibatis.submitted.xml_external_ref;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ShortNameTest {
    @Test
    void getStatementByShortName() throws Exception {
        Configuration configuration = getConfiguration();
        // statement can be referenced by its short name.
        MappedStatement selectPet = configuration.getMappedStatement("selectPet");
        assertNotNull(selectPet);
    }

    @Test
    void ambiguousShortNameShouldFail() throws Exception {
        Configuration configuration = getConfiguration();
        // ambiguous short name should throw an exception.
        Assertions.assertThrows(IllegalArgumentException.class, () -> configuration.getMappedStatement("select"));
    }

    private Configuration getConfiguration() throws IOException {
        try (Reader configReader = Resources.getResourceAsReader("org/apache/ibatis/submitted/xml_external_ref/MapperConfig.xml")) {
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configReader);
            return sqlSessionFactory.getConfiguration();
        }
    }
}
