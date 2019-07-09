
package org.apache.ibatis.parsing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.io.Resources;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xml.sax.*;

class XPathParserTest {
  private String resource = "resources/nodelet_test.xml";
  XPathParser parser ;
  /**
   <employee id="${id_var}">
     <blah something="that"/>
     <first_name>Jim</first_name>
     <last_name>Smith</last_name>
     <birth_date>
       <year>1970</year>
       <month>6</month>
       <day>15</day>
     </birth_date>
     <height units="ft">5.8</height>
     <weight units="lbs">200</weight>
     <active>true</active>
   </employee>
  */

  private void testEvalMethod(XPathParser parser) {
    assertEquals((Long) 1970L, parser.evalLong("/employee/birth_date/year"));
    assertEquals((short) 6, (short) parser.evalShort("/employee/birth_date/month"));
    assertEquals((Integer) 15, parser.evalInteger("/employee/birth_date/day"));
    assertEquals((Float) 5.8f, parser.evalFloat("/employee/height"));
    assertEquals((Double) 5.8d, parser.evalDouble("/employee/height"));
    assertEquals("${id_var}", parser.evalString("/employee/@id"));
    assertEquals(Boolean.TRUE, parser.evalBoolean("/employee/active"));
    assertEquals("<id>${id_var}</id>", parser.evalNode("/employee/@id").toString().trim());
    assertEquals(7, parser.evalNodes("/employee/*").size());
    XNode node = parser.evalNode("/employee/height");
    assertEquals("employee/height", node.getPath());
    assertEquals("employee[${id_var}]_height", node.getValueBasedIdentifier());
  }
  @AfterEach
  void after(){
    testEvalMethod(parser);
  }

  @Test /** 4 个参数 */
  void constructorWithInputStreamValidationVariablesEntityResolver() throws Exception {
    try (InputStream inputStream = Resources.getResourceAsStream(resource)) {
      parser = new XPathParser(inputStream, false, null, null);
    }
  }

  @Test  /** 3 个参数 */
  void constructorWithInputStreamValidationVariables() throws IOException {
    try (InputStream inputStream = Resources.getResourceAsStream(resource)) {
       parser = new XPathParser(inputStream, false, null);
    }
  }

  @Test  /** 2 个参数 */
  void constructorWithInputStreamValidation() throws IOException {
    try (InputStream inputStream = Resources.getResourceAsStream(resource)) {
       parser = new XPathParser(inputStream, false);
    }
  }

  @Test  /** 1 个参数 */
  void constructorWithInputStream() throws IOException {
    try (InputStream inputStream = Resources.getResourceAsStream(resource)) {
       parser = new XPathParser(inputStream);
    }
  }

  //Reader Source
  @Test
  void constructorWithReaderValidationVariablesEntityResolver() throws Exception {
    try (Reader reader = Resources.getResourceAsReader(resource)) {
       parser = new XPathParser(reader, false, null, null);
    }
  }

  @Test
  void constructorWithReaderValidationVariables() throws IOException {
    try (Reader reader = Resources.getResourceAsReader(resource)) {
       parser = new XPathParser(reader, false, null);
    }
  }

  @Test
  void constructorWithReaderValidation() throws IOException {
    try (Reader reader = Resources.getResourceAsReader(resource)) {
       parser = new XPathParser(reader, false);
    }
  }

  @Test
  void constructorWithReader() throws IOException {
    try (Reader reader = Resources.getResourceAsReader(resource)) {
       parser = new XPathParser(reader);
    }
  }

  //Xml String Source
  @Test
  void constructorWithStringValidationVariablesEntityResolver() throws Exception {
     parser = new XPathParser(getXmlString(resource), false, null, null);
  }

  @Test
  void constructorWithStringValidationVariables() throws IOException {
     parser = new XPathParser(getXmlString(resource), false, null);
  }

  @Test
  void constructorWithStringValidation() throws IOException {
     parser = new XPathParser(getXmlString(resource), false);
  }

  @Test
  void constructorWithString() throws IOException {
     parser = new XPathParser(getXmlString(resource));
  }

  //Document Source
  @Test
  void constructorWithDocumentValidationVariablesEntityResolver() {
     parser = new XPathParser(getDocument(resource), false, null, null);
  }

  @Test
  void constructorWithDocumentValidationVariables() {
     parser = new XPathParser(getDocument(resource), false, null);
  }

  @Test
  void constructorWithDocumentValidation() {
     parser = new XPathParser(getDocument(resource), false);
  }

  @Test
  void constructorWithDocument() {
     parser = new XPathParser(getDocument(resource));
  }

  private Document getDocument(String resource) {
    try {
      InputSource inputSource = new InputSource(Resources.getResourceAsReader(resource));
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(false);
      factory.setIgnoringComments(true);
      factory.setIgnoringElementContentWhitespace(false);
      factory.setCoalescing(false);
      factory.setExpandEntityReferences(true);
      DocumentBuilder builder = factory.newDocumentBuilder();
      return builder.parse(inputSource);//already closed resource in builder.parse method
    } catch (Exception e) {
      throw new BuilderException("Error creating document instance.  Cause: " + e, e);
    }
  }

  private String getXmlString(String resource) throws IOException {
    try (BufferedReader bufferedReader = new BufferedReader(Resources.getResourceAsReader(resource))) {
      StringBuilder sb = new StringBuilder();
      String temp;
      while ((temp = bufferedReader.readLine()) != null) {
        sb.append(temp);
      }
      return sb.toString();
    }
  }

}
