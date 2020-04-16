package org.apache.ibatis.plugin;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2020/4/16.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/4/16---13:33
 */
public abstract class BaseTest {

  AlwaysMapPlugin alwaysMapPlugin = new AlwaysMapPlugin();
  MorePlugin morePlugin = new MorePlugin();

  Map map = new HashMap();


}
