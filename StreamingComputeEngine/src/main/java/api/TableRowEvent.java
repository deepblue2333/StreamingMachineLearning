package api;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class TableRowEvent implements Event {
    // 字段存储核心结构：字段名 -> 类型值容器
    private final Map<String, Cell> fields = new HashMap<>();

    // 添加字段（带类型校验）
    public void addField(String fieldName, DataType dataType, Object value) {
        validateField(fieldName, dataType, value);
        fields.put(fieldName, new Cell(dataType, value));
    }

    // 类型安全获取字段值（泛型方法）
    @SuppressWarnings("unchecked")
    public <T> T getField(String fieldName, DataType dataType) {
        Cell cell = fields.get(fieldName);
        validateFieldExists(fieldName, cell);
        validateDataType(fieldName, dataType, cell);
        return (T) dataType.getClassType().cast(cell.getValue());
    }

    public void updateField(String fieldName, Object value) {
        Cell existingCell = fields.get(fieldName);
        validateFieldExists(fieldName, existingCell);
        DataType dataType = existingCell.getDataType();

        // 允许更新为null值（根据需求可选）
        if (value == null) {
            fields.put(fieldName, new Cell(dataType, null)); // 保持原类型
            return;
        }

        // 非空时校验类型
        if (!dataType.getClassType().isInstance(value)) {
            throw new IllegalArgumentException("字段类型不匹配 [" + fieldName + "] 期望："
                    + dataType.getClassType().getSimpleName() + " 实际："
                    + value.getClass().getSimpleName());
        }
        fields.put(fieldName, new Cell(dataType, value));
    }

    // 新增方法：通过字段名获取数据类型
    public DataType getDataType(String fieldName) {
        Cell cell = fields.get(fieldName);
        validateFieldExists(fieldName, cell);
        return cell.getDataType();
    }

    // 新增方法：安全获取数据类型（返回Optional）
    public Optional<DataType> getDataTypeSafe(String fieldName) {
        return Optional.ofNullable(fields.get(fieldName))
                .map(Cell::getDataType);
    }

    // 新增方法：批量获取字段类型映射
    public Map<String, DataType> getDataTypes() {
        return Collections.unmodifiableMap(
                fields.entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                e -> e.getValue().getDataType()
                        ))
        );
    }

    // 便捷方法：获取字符串类型字段
    public String getString(String fieldName) {
        return getField(fieldName, DataType.STRING);
    }

    // 便捷方法：获取整型字段
    public Integer getInt(String fieldName) {
        return getField(fieldName, DataType.INT);
    }

    // 便捷方法：获取日期字段
    public LocalDate getDate(String fieldName) {
        return getField(fieldName, DataType.DATE);
    }

    // 便捷方法：获取双精度字段
    public Double getDouble(String fieldName) {
        return getField(fieldName, DataType.DOUBLE);
    }

    // 获取所有字段名（不可修改视图）
    public Set<String> getFieldNames() {
        return Collections.unmodifiableSet(fields.keySet());
    }

    // 字段校验逻辑
    private void validateField(String fieldName, DataType dataType, Object value) {
        if (fieldName == null || fieldName.trim().isEmpty()) {
            throw new IllegalArgumentException("字段名不能为空");
        }
        if (dataType == null) {
            throw new IllegalArgumentException("数据类型不能为空");
        }
        // 允许 value == null

        if (value != null && !dataType.getClassType().isInstance(value)) { // 仅非空时校验类型
            throw new IllegalArgumentException("字段类型不匹配 [" + fieldName + "] 期望："
                    + dataType.getClassType().getSimpleName() + " 实际："
                    + value.getClass().getSimpleName());
        }
    }

    // 存在性校验
    private void validateFieldExists(String fieldName, Cell cell) {
        if (cell == null) {
            throw new IllegalArgumentException("字段不存在: " + fieldName);
        }
    }

    // 数据类型校验
    private void validateDataType(String fieldName, DataType expected, Cell cell) {
        if (cell.getDataType() != expected) {
            throw new IllegalArgumentException("类型不匹配 [" + fieldName + "] 期望："
                    + expected + " 实际：" + cell.getDataType());
        }
    }

    // 内部数据类型容器
    private static class Cell {
        private final DataType dataType;
        private final Object value;

        public Cell(DataType dataType, Object value) {
            this.dataType = dataType;
            this.value = value;
        }

        public DataType getDataType() {
            return dataType;
        }

        public Object getValue() {
            return value;
        }
    }

    // 数据类型枚举定义
    public enum DataType {
        STRING(String.class),
        INT(Integer.class),
        DATE(LocalDate.class),
        DOUBLE(Double.class);

        private final Class<?> classType;

        DataType(Class<?> classType) {
            this.classType = classType;
        }

        public Class<?> getClassType() {
            return classType;
        }
    }
}