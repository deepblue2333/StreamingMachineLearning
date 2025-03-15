package algo;

import api.MachineLearningRowEvent;
import api.Model;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class OnlineARIMA implements Model {
    private final int p, d, q;
    private final Queue<Double> rawDataWindow;  // 原始数据窗口（用于差分）
    private final Queue<Double> diffDataWindow;  // 差分后数据窗口（AR特征）
    private final Queue<Double> errorWindow;    // 预测误差窗口（MA特征）
    private RealVector weights;                 // 模型参数（AR + MA 系数）
    private final double learningRate;          // 学习率

    public OnlineARIMA(int p, int d, int q, int windowSize, double learningRate) {
        this.p = p;
        this.d = d;
        this.q = q;
        this.learningRate = learningRate;

        // 初始化窗口
        this.rawDataWindow = new LinkedList<>();
        this.diffDataWindow = new LinkedList<>();
        this.errorWindow = new LinkedList<>();

        // 初始化权重向量（AR项数 + MA项数 + 截距）
        this.weights = new ArrayRealVector(1 + p + q, 0.0);

        // 填充初始窗口（避免空指针）
        for (int i = 0; i < windowSize + d; i++) {
            rawDataWindow.add(0.0);
            diffDataWindow.add(0.0);
        }
        for (int i = 0; i < q; i++) {
            errorWindow.add(0.0);
        }
    }

    // 处理新数据点并更新模型
    public void update(double newValue) {
        // 1. 更新原始数据窗口
        rawDataWindow.add(newValue);
        rawDataWindow.poll();

        // 2. 计算差分（动态 d 阶差分）
        double[] diffValues = computeDiff(rawDataWindow, d);
        double currentDiff = diffValues[diffValues.length - 1];

        // 3. 更新差分数据窗口
        diffDataWindow.add(currentDiff);
        diffDataWindow.poll();

        // 4. 构建特征向量（AR特征 + MA误差特征 + 截距）
        RealVector features = buildFeatures();

        // 5. 计算预测误差（使用上一时刻的预测值和当前实际值）
        double lastPrediction = predict(); // 预测的是当前差分值
        double error = lastPrediction - currentDiff;
        errorWindow.add(error);
        errorWindow.poll();

        // 6. SGD 更新权重
        double prediction = features.dotProduct(weights);
        double gradient = prediction - currentDiff;
        weights = weights.subtract(features.mapMultiply(learningRate * gradient));
    }

    // 预测下一时刻的差分值
    public double predict() {
        RealVector features = buildFeatures();
        return features.dotProduct(weights);
    }

    // 获取最终预测值（差分逆变换）
    public double forecast() {
        // 预测差分值
        double diffPrediction = predict();

        // 逆差分计算（d 阶）
        double[] rawData = rawDataWindow.stream().mapToDouble(Double::doubleValue).toArray();
        return inverseDiff(rawData, diffPrediction, d);
    }

    //--- 工具方法 ---
    private RealVector buildFeatures() {
        // 特征包括：截距项（1.0） + AR滞后项 + MA滞后误差
        RealVector features = new ArrayRealVector(1 + p + q);
        features.setEntry(0, 1.0); // 截距

        // AR特征（p 个滞后差分值）
        int idx = 1;
        for (Double val : diffDataWindow) {
            if (idx > p) break;
            features.setEntry(idx++, val);
        }

        // MA特征（q 个滞后误差）
        for (Double err : errorWindow) {
            if (idx > p + q) break;
            features.setEntry(idx++, err);
        }

        return features;
    }

    // 计算 d 阶差分
    private double[] computeDiff(Queue<Double> data, int order) {
        double[] values = data.stream().mapToDouble(Double::doubleValue).toArray();
        for (int i = 0; i < order; i++) {
            for (int j = values.length - 1; j > i; j--) {
                values[j] = values[j] - values[j - 1];
            }
        }
        return values;
    }

    // 差分逆变换（恢复原始值）
    private double inverseDiff(double[] history, double diffPrediction, int order) {
        double[] reconstructed = new double[history.length + 1];
        System.arraycopy(history, 0, reconstructed, 0, history.length);
        reconstructed[history.length] = diffPrediction;

        for (int i = 0; i < order; i++) {
            for (int j = history.length; j > i; j--) {
                reconstructed[j] = reconstructed[j] + reconstructed[j - 1];
            }
        }
        return reconstructed[reconstructed.length - 1];
    }

    @Override
    public MachineLearningRowEvent predict(MachineLearningRowEvent event) {
        String singlePredictionField;

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
        Double predictValue = predict();
        event.updateField(singlePredictionField, predictValue);
        return event;
    }

    @Override
    public void update(MachineLearningRowEvent event) {
        String singleFeatureField;

        Set<String> featureFields = event.getPredictionFields();
        // 判断是否仅包含一个元素
        if (featureFields.size() == 1) {
            // 获取唯一元素
            singleFeatureField = featureFields.iterator().next();
            System.out.println("唯一特征字段: " + singleFeatureField);
        } else {
            // 处理不符合预期的情况
            throw new IllegalStateException("特征字段数量异常，期望1个，实际：" + featureFields.size());
        }
        Double streamDataValue = (Double) event.getDouble(singleFeatureField);
        // 更新模型
        update(streamDataValue);
    }

    // 示例用法
    public static void main(String[] args) {
        OnlineARIMA model = new OnlineARIMA(2, 1, 1, 10, 0.01);
        double[] streamData = {10.1, 10.5, 11.0, 10.8, 11.2, 11.5, 11.7, 11.9, 12.0, 12.2};

        for (double data : streamData) {
            model.update(data);
            System.out.printf("Raw: %.2f | Diff预测: %.2f | 最终预测: %.2f%n",
                    data, model.predict(), model.forecast());
        }
    }

}