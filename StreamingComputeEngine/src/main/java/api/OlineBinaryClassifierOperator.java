package api;

import algo.OnlineARIMA;
import algo.OnlineBinaryClassifier;
import job.Logger;

public class OlineBinaryClassifierOperator  extends TrainOperator {

    public OlineBinaryClassifierOperator(String name, int parallelism, double learningRate) {
        super(name, parallelism, new OnlineBinaryClassifier(learningRate));
    }

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
        this.instance = instance;
    }

    @Override
    public void apply(Event event, EventCollector eventCollector) {
        MachineLearningRowEvent mle = (MachineLearningRowEvent) event;

        if (mle.hasFeatureFields() && mle.hasPredictionFields()) {
            update(mle);
            // 评估模型效果
            MachineLearningRowEvent e = predict(mle);
            eventCollector.add(e);
            Logger.log("OnlineARIMAOperator model predict for estimation");
        } else if (mle.hasPredictionFields()) {
            MachineLearningRowEvent e = predict(mle);
            eventCollector.add(e);
            Logger.log("OnlineARIMAOperator model predict");
        } else {
            update((MachineLearningRowEvent) event);
            Logger.log("OnlineARIMAOperator update model");
        }


    }
}
