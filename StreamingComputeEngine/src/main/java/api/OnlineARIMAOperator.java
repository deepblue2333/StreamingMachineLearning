package api;

import algo.OnlineARIMA;

public class OnlineARIMAOperator extends TrainOperator {
    public OnlineARIMAOperator(String name, int parallelism, Model model) {
        super(name, parallelism, model);
    }

//    public OnlineARIMAOperator(String name, int parallelism, int p, int d, int q, int windowSize, int learningRate) {
//        super(name, parallelism, new OnlineARIMA(p, d, q, windowSize, learningRate));
//    }

    @Override
    public void update(MachineLearningRowEvent event) {
        Model model = getModel();
        model.update(event);
    }

    @Override
    public MachineLearningRowEvent predict(MachineLearningRowEvent event) {
        Model model = getModel();
        return model.predict(event);
    }

    @Override
    public void setupInstance(int instance) {

    }

    @Override
    public void apply(Event event, EventCollector eventCollector) {

    }
}
