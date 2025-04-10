package api;

public class PrintSink extends Operator {
    private int instance;

    public PrintSink(String name, int parallelism) {
        super(name, parallelism);
    }

    @Override
    public void setupInstance(int instance) {
        this.instance = instance;
    }

    @Override
    public void apply(Event event, EventCollector eventCollector) {
        System.out.println(instance);
        System.out.println(event);
    }
}
