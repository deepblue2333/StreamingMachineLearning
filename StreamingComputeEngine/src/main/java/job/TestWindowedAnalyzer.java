package job;

import api.Event;
import api.EventCollector;
import api.EventWindow;
import api.GroupingStrategy;
import api.WindowOperator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

class TestWindowedAnalyzer extends WindowOperator {
  private int instance;

  public TestWindowedAnalyzer(String name, int parallelism, GroupingStrategy grouping) {
    super(name, parallelism, grouping);
  }

  @Override
  public void setupInstance(int instance) {
    this.instance = instance;
  }

  @Override
  public void apply(EventWindow window, EventCollector eventCollector) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    sdf.setTimeZone(TimeZone.getTimeZone("GMT+8")); // 东八区

    Logger.log(String.format("%d transactions are received between %s and %s\n",
        window.getEvents().size(), sdf.format(new Date(window.getStartTime())), sdf.format(new Date(window.getEndTime()))));
    int counter = 0;
    for (Event event: window.getEvents()) {
      counter++;
      Logger.log(String.format("Event: %s\n", event));
    }

    Logger.log(String.format("Counter: %s\n", counter));
  }
}
