package api;

import java.util.Set;

public class MachineLearningRowEventTest {
    public static void main(String[] args) {
        MachineLearningRowEvent event = new MachineLearningRowEvent();

        // 添加特征字段
        event.addField("age", TableRowEvent.DataType.INT, MachineLearningRowEvent.FieldType.FEATURE, 30);
        event.addField("income", TableRowEvent.DataType.DOUBLE, MachineLearningRowEvent.FieldType.FEATURE, 50000.0);

        // 添加预测值和真实值字段
        event.addField("predicted_salary", TableRowEvent.DataType.DOUBLE, MachineLearningRowEvent.FieldType.PREDICTION, 75000.0);
        event.addField("actual_salary", TableRowEvent.DataType.DOUBLE, MachineLearningRowEvent.FieldType.GROUND_TRUTH, 72000.0);

        // 获取特征字段
        Set<String> features = event.getFeatureFields();
        System.out.println("Features: " + features); // 输出 [age, income]

        // 获取预测值字段
        Double predictedSalary = event.getDouble("predicted_salary");
        System.out.println("Predicted Salary: " + predictedSalary);

        System.out.println("Actual Salary: " + event.getDouble("actual_salary"));
    }
}
