
package org.apache.ibatis.executor;

import org.apache.ibatis.transaction.Transaction;
import org.junit.jupiter.api.Test;

class CachingBatchExecutorTest extends BaseExecutorTest {

  @Test
  void dummy() {
  }

  @Override
  protected Executor createExecutor(Transaction transaction) {
    return new CachingExecutor(new BatchExecutor(config, transaction));
  }

}
