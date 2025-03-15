package api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class TableRowEventTest {
    private TableRowEvent event;
    private final LocalDate testDate = LocalDate.of(2023, 12, 31);

    @BeforeEach
    void setUp() {
        event = new TableRowEvent();
        // 初始化测试数据
        event.addField("name", TableRowEvent.DataType.STRING, "张三");
        event.addField("age", TableRowEvent.DataType.INT, 30);
        event.addField("birthday", TableRowEvent.DataType.DATE, testDate);
        event.addField("salary", TableRowEvent.DataType.DOUBLE, 15000.50);
    }

    // 正常功能测试
    @Test
    void shouldStoreAndRetrieveString() {
        assertEquals("张三", event.getString("name"));
    }

    @Test
    void shouldStoreAndRetrieveInteger() {
        assertEquals(30, event.getInt("age"));
    }

    @Test
    void shouldStoreAndRetrieveDate() {
        assertEquals(testDate, event.getDate("birthday"));
    }

    @Test
    void shouldStoreAndRetrieveDouble() {
        assertEquals(15000.50, event.getDouble("salary"), 0.001);
    }

    // 异常情况测试
    @Test
    void shouldThrowWhenAddNullValue() {
        assertThrows(IllegalArgumentException.class, () ->
                event.addField("test", TableRowEvent.DataType.STRING, null)
        );
    }

    @Test
    void shouldThrowWhenTypeMismatchOnAdd() {
        assertThrows(IllegalArgumentException.class, () ->
                event.addField("test", TableRowEvent.DataType.INT, "string value")
        );
    }

    @Test
    void shouldThrowWhenGetNonexistentField() {
        assertThrows(IllegalArgumentException.class, () ->
                event.getString("nonexistent")
        );
    }

    @Test
    void shouldThrowWhenTypeMismatchOnGet() {
        assertThrows(IllegalArgumentException.class, () ->
                event.getDate("name")  // name字段实际是STRING类型
        );
    }

    // 边界条件测试
    @Test
    void shouldHandleEmptyFieldName() {
        assertThrows(IllegalArgumentException.class, () ->
                event.addField("", TableRowEvent.DataType.STRING, "value")
        );
    }

    @Test
    void shouldHandleDuplicateField() {
        event.addField("age", TableRowEvent.DataType.INT, 35);
        assertEquals(35, event.getInt("age"));
    }

    // 辅助方法测试
    @Test
    void shouldReturnFieldNames() {
        var names = event.getFieldNames();
        assertTrue(names.contains("name"));
        assertTrue(names.contains("age"));
        assertTrue(names.contains("birthday"));
        assertTrue(names.contains("salary"));
        assertEquals(4, names.size());
    }

    @Test
    void shouldReturnUnmodifiableFieldNames() {
        var names = event.getFieldNames();
        assertThrows(UnsupportedOperationException.class, () ->
                names.add("newField")
        );
    }

    // 泛型方法测试
    @Test
    void shouldUseGenericMethodCorrectly() {
        Double salary = event.getField("salary", TableRowEvent.DataType.DOUBLE);
        assertEquals(15000.50, salary, 0.001);
    }

    @Test
    void shouldThrowWithWrongGenericType() {
        assertThrows(ClassCastException.class, () -> {
            Integer salary = event.getField("salary", TableRowEvent.DataType.DOUBLE);
        });
    }

    // 日期边界测试
    @Test
    void shouldHandleLeapDate() {
        LocalDate leapDate = LocalDate.of(2020, 2, 29);
        event.addField("leap", TableRowEvent.DataType.DATE, leapDate);
        assertEquals(leapDate, event.getDate("leap"));
    }

    // 浮点数精度测试
    @Test
    void shouldHandleDoublePrecision() {
        event.addField("precise", TableRowEvent.DataType.DOUBLE, 0.1 + 0.2);
        assertEquals(0.3, event.getDouble("precise"), 0.0000001);
    }
}