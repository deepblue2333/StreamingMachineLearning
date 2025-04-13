package engine;

import api.Event;
import api.GroupingStrategy;

/**
 * EventDispatcher is responsible for transporting events from
 * the incoming queue to the outgoing queues with a grouping strategy.
 */
public class EventDispatcher extends Process {
  private final OperatorExecutor downstreamExecutor;
  private EventQueue incomingQueue = null;
  private EventQueue [] outgoingQueues = null;

  public EventDispatcher(OperatorExecutor downstreamExecutor) {
    this.downstreamExecutor = downstreamExecutor;
  }

  @Override
  boolean runOnce() {
    try {
      Event event = incomingQueue.take();

      GroupingStrategy grouping = downstreamExecutor.getGroupingStrategy();
      int instance = grouping.getInstance(event, outgoingQueues.length);
      if (instance == -1) {
          for (EventQueue outgoingQueue : outgoingQueues) {
              outgoingQueue.put(event);
          }
      } else {
        outgoingQueues[instance].put(event);
      }
    } catch (InterruptedException e) {
      return false;
    }
    return true;
  }

  public void setIncomingQueue(EventQueue queue) {
    incomingQueue = queue;
  }

  public void setOutgoingQueues(EventQueue [] queues) {
    outgoingQueues = queues;
  }
}
