package api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TableRowEventNullTest {
    private TableRowEvent event;

    @BeforeEach
    void setUp() {
        event = new TableRowEvent();
        event.addField("optionalInt", TableRowEvent.DataType.INT, null); // 允许null的字段
        event.addField("requiredString", TableRowEvent.DataType.STRING, "default");
    }

    // 测试允许空值的字段操作
    @Test
    void should_handle_nullable_fields_correctly() {
        // 验证初始null值
        assertNull(event.getInt("optionalInt"));

        // 更新为合法值
        event.updateField("optionalInt", 42);
        assertEquals(42, event.getInt("optionalInt"));

        // 重置为null
        event.updateField("optionalInt", null);
        assertNull(event.getInt("optionalInt"));
    }

    // 测试类型校验
    @Test
    void should_throw_when_update_with_wrong_type() {
        // 验证错误类型更新
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> event.updateField("optionalInt", "42"));

        assertTrue(ex.getMessage().contains("期望：Integer"));
        assertTrue(ex.getMessage().contains("实际：String"));
    }

    // TODO: 测试非空字段约束

    //    @Test
    //    void should_reject_null_for_required_fields() {
    //        // 非空字段设置null
    //        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
    //                () -> event.updateField("requiredString", null));
    //
    //        assertTrue(ex.getMessage().contains("不允许为null"));
    //    }

    // 测试字段存在性校验
    @Test
    void should_validate_field_existence() {
        assertThrows(IllegalArgumentException.class,
                () -> event.updateField("nonExistentField", 123));
    }

    // 测试类型安全获取方法
    @Test
    void should_enforce_type_safe_access() {
        // 正确类型访问
        assertDoesNotThrow(() -> event.getString("requiredString"));

        // 错误类型访问
        assertThrows(IllegalArgumentException.class,
                () -> event.getInt("requiredString"));
    }
}