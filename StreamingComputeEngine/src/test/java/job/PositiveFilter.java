package job;

import api.*;

public class PositiveFilter extends MapOperator {
    public PositiveFilter(String name, int parallelism, GroupingStrategy grouping) {
        super(name, parallelism, grouping);
    }

    public PositiveFilter(String name, int parallelism) {
        super(name, parallelism);
    }

    @Override
    public void apply(Event event, EventCollector eventCollector) {
        TableRowEvent t = (TableRowEvent) event;
        if (t.getInt("trans_class") == 1) {
            eventCollector.add(t);
        }
    }
}
