package api;

import job.Logger;

import java.util.Map;

public class MachineLearningRowConvertOperator extends Operator {
    private int instance;
    private Map<String, MachineLearningRowEvent.FieldType> fieldTypes;

    public MachineLearningRowConvertOperator(String name, int parallelism, Map<String, MachineLearningRowEvent.FieldType> fieldTypes) {
        super(name, parallelism);
        this.fieldTypes = fieldTypes;
    }

    public MachineLearningRowConvertOperator(String name, int parallelism, GroupingStrategy grouping,  Map<String, MachineLearningRowEvent.FieldType> fieldTypes) {
        super(name, parallelism, grouping);
        this.fieldTypes = fieldTypes;
    }

    @Override
    public void setupInstance(int instance) {
        this.instance = instance;
    }

    @Override
    public void apply(Event event, EventCollector eventCollector) {
        TableRowEvent e = (TableRowEvent) event;
        MachineLearningRowEvent mle = new MachineLearningRowEvent(e, this.fieldTypes);
        eventCollector.add(mle);
        Logger.log(String.format("convert TableRowEvent to MachineLearningRowEvent: %s \n", mle));
    }
}
