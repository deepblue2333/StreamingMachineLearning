package job;

import api.Event;
import api.EventCollector;
import api.GroupingStrategy;
import api.Operator;

class WindowedTransactionCountAnalyzer extends Operator {
  private static final long serialVersionUID = 6494435291715923496L;
  private int instance;

  public WindowedTransactionCountAnalyzer(String name, int parallelism, GroupingStrategy grouping) {
    super(name, parallelism, grouping);
  }

  @Override
  public void setupInstance(int instance) {
    this.instance = instance;
  }

  @Override
  public void apply(Event transaction, EventCollector eventCollector) {
    TransactionEvent e = ((TransactionEvent)transaction);
    // Dummy analyzer. Allow all transactions.
    eventCollector.add(new TransactionScoreEvent(e, 0.0f));
  }
}
