package org.apache.ibatis.reflection.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 64274 on 2019/7/11.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/7/11---9:49
 */
public abstract class Parent<T extends Serializable> {

  protected T id;
  protected List<T> list;
  protected T[] array;
  private T fld;
  public T pubFld;

  public T getId() {
    return id;
  }

  public void setId(T id) {
    this.id = id;
  }

  public List<T> getList() {
    return list;
  }

  public void setList(List<T> list) {
    this.list = list;
  }

  public T[] getArray() {
    return array;
  }

  public void setArray(T[] array) {
    this.array = array;
  }

  public T getFld() {
    return fld;
  }

  public void setFld(T fld) {
    this.fld = fld;
  }
}
