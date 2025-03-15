package api;

public class AddPredictionFieldForEstimate extends Operator {
    private int instance;

    public AddPredictionFieldForEstimate(String name, int parallelism) {
        super(name, parallelism);
    }

    public AddPredictionFieldForEstimate(String name, int parallelism, GroupingStrategy grouping) {
        super(name, parallelism, grouping);
    }

    @Override
    public void setupInstance(int instance) {
        this.instance = instance;
    }

    @Override
    public void apply(Event event, EventCollector eventCollector) {
        MachineLearningRowEvent mle = (MachineLearningRowEvent) event;
        mle.addField("pre_next", TableRowEvent.DataType.DOUBLE, MachineLearningRowEvent.FieldType.PREDICTION, null);
        eventCollector.add(mle);
    }
}
