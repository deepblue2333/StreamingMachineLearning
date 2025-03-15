package job;

import api.*;
import engine.JobStarter;

import java.util.HashMap;
import java.util.Map;

public class CsvReaderSourceTest {
    public static void main(String[] args) {
        Job job = new Job("CsvReaderSourceTest_Job");
        String directory = "/Users/rain/Project/java/StreamingMachineLearning/StreamingComputeEngine/src/main/resources/data";

        CsvReaderSource csvReaderSource = new CsvReaderSource(
                "CsvReader",
                1,
                directory,
                ',',
                new TableRowEvent.DataType[]{
                        TableRowEvent.DataType.DOUBLE,
                        TableRowEvent.DataType.DOUBLE,
                        TableRowEvent.DataType.DOUBLE,
                        TableRowEvent.DataType.DOUBLE,
                        TableRowEvent.DataType.DOUBLE,
                        TableRowEvent.DataType.DOUBLE
                },
                new String[]{"Timestamp", "Open", "High", "Low", "Close", "Close"},
                true);
        Stream csvStream = job.addSource(csvReaderSource);

        Map<String, MachineLearningRowEvent.FieldType> fieldTypes = new HashMap<>();
        fieldTypes.put("Timestamp", MachineLearningRowEvent.FieldType.FEATURE);
        fieldTypes.put("Open", MachineLearningRowEvent.FieldType.GROUND_TRUTH);

        MachineLearningRowConvertOperator mlRowConverter =
                new MachineLearningRowConvertOperator("mlRowConverter", 1, fieldTypes);

        csvStream.applyOperator(mlRowConverter);

        JobStarter starter = new JobStarter(job);
        starter.start();
    }
}
