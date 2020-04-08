
package org.apache.ibatis.submitted.xml_external_ref;
import org.apache.common.MyBaseXmlConfig;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class ShortNameTest extends MyBaseXmlConfig {

  ShortNameTest() throws IOException {}

  Configuration configuration = getConfiguration("org/apache/ibatis/submitted/xml_external_ref/MapperConfig.xml");

  // statement can be referenced by its short name.
  @Test
    void getStatementByShortName() {
        MappedStatement selectPet = configuration.getMappedStatement("selectPet");
        assertNotNull(selectPet);
    }

  // ambiguous short name should throw an exception.
    @Test
    void ambiguousShortNameShouldFail() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> configuration.getMappedStatement("select"));
    }

}
