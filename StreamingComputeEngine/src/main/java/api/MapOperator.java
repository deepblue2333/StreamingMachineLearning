package api;

public class MapOperator extends Operator {
    private int instance;

    public MapOperator(String name, int parallelism) {
        super(name, parallelism);
    }

    public MapOperator(String name, int parallelism, GroupingStrategy grouping) {
        super(name, parallelism, grouping);
    }

    public void setupInstance(int instance) {
        this.instance = instance;
    }

    @Override
    public void apply(Event event, EventCollector eventCollector) {

    }
}
