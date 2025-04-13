package job;

import api.*;
import kotlin.collections.Grouping;

public class TransMap extends MapOperator {

    public TransMap(String name, int parallelism, GroupingStrategy grouping) {
        super(name, parallelism, grouping);
    }

    public TransMap(String name, int parallelism) {
        super(name, parallelism);
    }

    @Override
    public void apply(Event event, EventCollector eventCollector) {
//        System.out.println("TransMap");
        TableRowEvent t = (TableRowEvent) event;

        String class_type = t.getString("Class");

        if(class_type.equals("\"0\"")) {
            t.addField("trans_class", TableRowEvent.DataType.INT, 0);
        } else if(class_type.equals("\"1\"")) {
            t.addField("trans_class", TableRowEvent.DataType.INT, 1);
        }

        eventCollector.add(event);
    }
}
