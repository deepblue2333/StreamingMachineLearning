package algo;

import api.MachineLearningRowEvent;
import api.Model;
import api.Operator;
import job.Logger;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class OnlineBinaryClassifier implements Model, Serializable {
    private final Map<String, Double> weights; // 特征权重
    private final double learningRate;
    private static final Random rand = new Random();

    public OnlineBinaryClassifier(double learningRate) {
        this.weights = new HashMap<>();
        this.learningRate = learningRate;
    }

    // 预测概率（Logistic函数）
    public double predictProbability(Map<String, Double> features) {
        double score = 0.0;
        for (Map.Entry<String, Double> entry : features.entrySet()) {
            score += weights.getOrDefault(entry.getKey(), 0.0) * entry.getValue();
        }
        return 1.0 / (1.0 + Math.exp(-score));
    }

    // 更新模型
    public void update(Map<String, Double> features, int label) {
        double predictedProb = predictProbability(features);
        double error = predictedProb - label; // 梯度计算

        for (Map.Entry<String, Double> entry : features.entrySet()) {
            String feature = entry.getKey();
            double value = entry.getValue();
            double currentWeight = weights.getOrDefault(feature, 0.0);
            weights.put(feature, currentWeight - learningRate * error * value);
        }
    }

    @Override
    public MachineLearningRowEvent predict(MachineLearningRowEvent event) {
        Map<String, Double> features = new HashMap<>();
        String singlePredictionField;

        Set<String> featureFields = event.getFeatureFields();

        for (String field : featureFields) {
            features.put(field, event.getDouble(field));
        }

        Set<String> predictionFields = event.getPredictionFields();
        // 判断是否仅包含一个元素
        if (predictionFields.size() == 1) {
            // 获取唯一元素
            singlePredictionField = predictionFields.iterator().next();
            System.out.println("唯一预测字段: " + singlePredictionField);
        } else {
            // 处理不符合预期的情况
            throw new IllegalStateException("预测字段数量异常，期望1个，实际：" + predictionFields.size());
        }

        double prob = predictProbability(features);
        int predictedLabel = prob > 0.5 ? 1 : 0;

        event.updateField(singlePredictionField, predictedLabel);

        Logger.log(String.format("预测值为：%s\n", predictedLabel));

        return event;
    }

    @Override
    public void update(MachineLearningRowEvent event) {
        String singleFeatureField;
        String LabelField;
        Map<String, Double> features = new HashMap<>();

        Set<String> featureFields = event.getFeatureFields();

        for (String field : featureFields) {
            features.put(field, event.getDouble(field));
        }

        LabelField = event.getPredictionFields().iterator().next();
        int trueLabel = event.getInt(LabelField);

        // 更新模型
        update(features, trueLabel);
    }

    public static void main(String[] args) {
        OnlineBinaryClassifier model = new OnlineBinaryClassifier(0.01);
        int correct = 0;

        // 模拟流式数据（1000个样本）
        for (int i = 0; i < 1000; i++) {
            // 生成特征和标签
            Map<String, Double> features = new HashMap<>();
            double x1 = rand.nextGaussian();
            double x2 = rand.nextGaussian();
            features.put("x1", x1);
            features.put("x2", x2);
            int trueLabel = (2 * x1 - x2 + rand.nextGaussian()) > 0 ? 1 : 0;

            // 预测并更新模型
            double prob = model.predictProbability(features);
            int predictedLabel = prob > 0.5 ? 1 : 0;
            model.update(features, trueLabel);

            // 计算准确率
            if (predictedLabel == trueLabel) correct++;
            if (i % 100 == 99) {
                double accuracy = (double) correct / (i + 1);
                System.out.printf("样本数: %d, 准确率: %.4f%n", i + 1, accuracy);
            }
        }
    }
}