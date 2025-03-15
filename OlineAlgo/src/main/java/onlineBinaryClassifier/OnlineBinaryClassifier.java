package onlineBinaryClassifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class OnlineBinaryClassifier {
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