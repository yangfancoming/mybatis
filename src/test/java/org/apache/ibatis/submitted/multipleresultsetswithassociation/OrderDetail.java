
package org.apache.ibatis.submitted.multipleresultsetswithassociation;

public class OrderDetail {

  private int orderId;
  private int lineNumber;
  private int quantity;
  private String itemDescription;

  private OrderHeader orderHeader;

  public int getOrderId() {
    return orderId;
  }

  public void setOrderId(int orderId) {
    this.orderId = orderId;
  }

  public int getLineNumber() {
    return lineNumber;
  }

  public void setLineNumber(int lineNumber) {
    this.lineNumber = lineNumber;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public String getItemDescription() {
    return itemDescription;
  }

  public void setItemDescription(String itemDescription) {
    this.itemDescription = itemDescription;
  }

  public OrderHeader getOrderHeader() {
    return orderHeader;
  }

  public void setOrderHeader(OrderHeader orderHeader) {
    this.orderHeader = orderHeader;
  }
}
