package api;

public class PrintSink extends Operator {
    private int instance;
    int cnt=0;

    public PrintSink(String name, int parallelism) {
        super(name, parallelism);
    }

    @Override
    public void setupInstance(int instance) {
        this.instance = instance;
    }

    @Override
    public void apply(Event event, EventCollector eventCollector) {
        System.out.println(getName() + ' ' + instance + " ：" + ++cnt);
        System.out.println(event);
    }
}
