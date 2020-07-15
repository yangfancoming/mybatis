package org.apache.ibatis.parsing;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

/**
 * Created by Administrator on 2019/12/3.
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/12/3---15:45
 */
public class MyErrorHandler implements ErrorHandler {

  @Override
  public void warning(SAXParseException exception)  {
    System.out.println("自定义ErrorHandler  warning.............");
  }

  @Override
  public void error(SAXParseException exception)  {
    System.out.println("自定义ErrorHandler error.............");
  }

  @Override
  public void fatalError(SAXParseException exception)  {
    System.out.println("自定义ErrorHandler fatalError.............");
  }

}
