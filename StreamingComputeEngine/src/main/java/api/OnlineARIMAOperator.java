package api;

import algo.OnlineARIMA;
import job.Logger;

import java.util.Set;

public class OnlineARIMAOperator extends TrainOperator {

    public OnlineARIMAOperator(String name, int parallelism, int p, int d, int q, int windowSize, int learningRate) {
        super(name, parallelism, new OnlineARIMA(p, d, q, windowSize, learningRate));
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
        String singleTruthField;

        MachineLearningRowEvent mle = (MachineLearningRowEvent) event;

        Set<String> truthFields = mle.getGroundTruthFields();
        // 判断是否仅包含一个元素
        if (truthFields.size() == 1) {
            // 获取唯一元素
            singleTruthField = truthFields.iterator().next();
            System.out.println("唯一真实值字段: " + singleTruthField);
        } else {
            // 处理不符合预期的情况
            throw new IllegalStateException("真实值字段数量异常，期望1个，实际：" + truthFields.size());
        }

        Double streamDataValue = (Double) mle.getDouble(singleTruthField);

        if (streamDataValue == null) {
            predict(mle);
            Logger.log("OnlineARIMAOperator model predict");
        } else {
            update((MachineLearningRowEvent) event);
            Logger.log("OnlineARIMAOperator update model");
        }
    }
}
