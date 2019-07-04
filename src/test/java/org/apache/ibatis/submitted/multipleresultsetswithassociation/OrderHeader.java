
package org.apache.ibatis.submitted.multipleresultsetswithassociation;

public class OrderHeader {

  private int orderId;
  private String custName;

  public int getOrderId() {
    return orderId;
  }

  public void setOrderId(int orderId) {
    this.orderId = orderId;
  }

  public String getCustName() {
    return custName;
  }

  public void setCustName(String custName) {
    this.custName = custName;
  }

}
