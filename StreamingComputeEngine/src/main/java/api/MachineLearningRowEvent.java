package api;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MachineLearningRowEvent extends TableRowEvent {

    // 定义字段用途类型枚举
    public enum FieldType {
        FEATURE,      // 特征字段
        PREDICTION,   // 预测值字段
        GROUND_TRUTH  // 真实值字段
    }

    public MachineLearningRowEvent() {}

    public MachineLearningRowEvent(TableRowEvent event, Map<String, FieldType> fieldTypes) {
        this.fieldTypes = fieldTypes;
        Set<String> fieldNames = event.getFieldNames();
        for (String fieldName : fieldNames) {
            addField(
                    fieldName,
                    event.getDataType(fieldName),
                    fieldTypes.get(fieldName),
                    event.getField(fieldName, event.getDataType(fieldName))
            );
        }
    }

    // 存储字段名到用途类型的映射
    private Map<String, FieldType> fieldTypes = new HashMap<>();

    // 覆盖父类addField方法，强制使用子类的方法
    @Override
    public void addField(String fieldName, DataType dataType, Object value) {
        throw new UnsupportedOperationException(
                "Please use addField(String fieldName, DataType dataType, FieldType fieldType, Object value) instead."
        );
    }

    // 新增方法：添加字段时指定用途类型
    public void addField(String fieldName, DataType dataType, FieldType fieldType, Object value) {
        // 调用父类方法进行原有校验和存储
        super.addField(fieldName, dataType, value);
        // 存储用途类型
        fieldTypes.put(fieldName, fieldType);
    }

    // 根据字段名获取用途类型
    public FieldType getFieldType(String fieldName) {
        if (!fieldTypes.containsKey(fieldName)) {
            throw new IllegalArgumentException("Field not found or type not specified: " + fieldName);
        }
        return fieldTypes.get(fieldName);
    }

    // 获取指定类型的所有字段名
    public Set<String> getFieldsByType(FieldType type) {
        return fieldTypes.entrySet().stream()
                .filter(entry -> entry.getValue() == type)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    // 便捷方法：获取所有特征字段名
    public Set<String> getFeatureFields() {
        return getFieldsByType(FieldType.FEATURE);
    }

    // 便捷方法：获取所有预测值字段名
    public Set<String> getPredictionFields() {
        return getFieldsByType(FieldType.PREDICTION);
    }

    // 便捷方法：获取所有真实值字段名
    public Set<String> getGroundTruthFields() {
        return getFieldsByType(FieldType.GROUND_TRUTH);
    }

    // 获取所有字段的用途类型（不可修改视图）
    public Map<String, FieldType> getAllFieldTypes() {
        return Collections.unmodifiableMap(fieldTypes);
    }

    // 新增存在性检查方法
    public boolean hasGroundTruthFields() {
        return !getGroundTruthFields().isEmpty();
    }

    public boolean hasPredictionFields() {
        return !getPredictionFields().isEmpty();
    }

    public boolean hasFeatureFields() {
        return !getFeatureFields().isEmpty();
    }
}