
package org.apache.ibatis.executor;

import org.apache.ibatis.transaction.Transaction;
import org.junit.jupiter.api.Test;

class ReuseExecutorTest extends BaseExecutorTest {

  @Test
  void dummy() {
  }

  @Override
  @Test
  public void shouldFetchPostWithBlogWithCompositeKey() throws Exception {
    super.shouldFetchPostWithBlogWithCompositeKey();
  }

  @Override
  protected Executor createExecutor(Transaction transaction) {
    return new ReuseExecutor(config,transaction);
  }
}
