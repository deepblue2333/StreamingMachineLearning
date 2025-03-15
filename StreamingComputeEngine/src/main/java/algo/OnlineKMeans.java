package algo;

import api.Model;
import api.OnlineARIMAOperator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class OnlineKMeans {
    private List<double[]> centroids;  // 聚类中心
    private List<Integer> counts;     // 每个聚类中心接收到的样本数
    private final int dimensions;     // 数据维度

    // 初始化模型
    public OnlineKMeans(int k, int dimensions) {
        this.dimensions = dimensions;
        this.centroids = new ArrayList<>();
        this.counts = new ArrayList<>();
        Random rand = new Random();

        // 随机初始化聚类中心 (此处范围为0-10，可根据数据特性调整)
        for (int i = 0; i < k; i++) {
            double[] centroid = new double[dimensions];
            for (int d = 0; d < dimensions; d++) {
                centroid[d] = rand.nextDouble() * 10;
            }
            centroids.add(centroid);
            counts.add(0);  // 初始计数为0
        }
    }

    // 更新模型：处理新数据点
    public void update(double[] point) {
        // 1. 找到最近的聚类中心
        int closestIdx = findClosestCentroid(point);

        // 2. 更新该聚类中心
        double[] centroid = centroids.get(closestIdx);
        int newCount = counts.get(closestIdx) + 1;
        double learningRate = 1.0 / newCount;  // 动态学习率

        // 更新每个维度的值
        for (int d = 0; d < dimensions; d++) {
            centroid[d] += learningRate * (point[d] - centroid[d]);
        }

        counts.set(closestIdx, newCount);  // 更新计数器
    }

    // 预测：返回数据点所属聚类的索引
    public int predict(double[] point) {
        return findClosestCentroid(point);
    }

    // 计算欧氏距离并找到最近聚类中心
    private int findClosestCentroid(double[] point) {
        int closestIdx = -1;
        double minDistance = Double.MAX_VALUE;

        for (int i = 0; i < centroids.size(); i++) {
            double distance = euclideanDistance(point, centroids.get(i));
            if (distance < minDistance) {
                minDistance = distance;
                closestIdx = i;
            }
        }
        return closestIdx;
    }

    // 计算欧氏距离
    private double euclideanDistance(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += Math.pow(a[i] - b[i], 2);
        }
        return Math.sqrt(sum);
    }

    public static void main(String[] args) {
        // 配置参数
        int dimensions = 2;    // 数据维度
        int k = 3;             // 聚类数量
        long numPoints = 1000000000000L;  // 模拟数据量

        // 初始化模型
        OnlineKMeans model = new OnlineKMeans(k, dimensions);

        // 模拟数据流：生成围绕三个中心的流式数据
        Random rand = new Random();
        double[][] trueCentroids = {{2.0, 2.0}, {8.0, 8.0}, {5.0, 5.0}};  // 真实中心

        for (long i = 0; i < numPoints; i++) {
            // 随机选择一个真实中心，生成带高斯噪声的数据点
            int cluster = rand.nextInt(k);
            double[] point = new double[dimensions];
            for (int d = 0; d < dimensions; d++) {
                point[d] = trueCentroids[cluster][d] + rand.nextGaussian();
            }

            // 在线更新模型
            model.update(point);
        }

        // 输出学习到的聚类中心
        System.out.println("Learned Centroids:");
        for (double[] centroid : model.centroids) {
            System.out.println(Arrays.toString(centroid));
        }

        // 在线预测示例
        double[] testPoint = {7.5, 7.5};
        int predictedCluster = model.predict(testPoint);
        System.out.println("\nTest Point: " + Arrays.toString(testPoint));
        System.out.println("Predicted Cluster: " + predictedCluster);
    }
}