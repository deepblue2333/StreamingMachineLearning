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
//        fieldTypes.put("Timestamp", MachineLearningRowEvent.FieldType.FEATURE);
        fieldTypes.put("Open", MachineLearningRowEvent.FieldType.FEATURE);

        MachineLearningRowConvertOperator mlRowConverter =
                new MachineLearningRowConvertOperator("mlRowConverter", 1, fieldTypes);

        Stream mleStream = csvStream.applyOperator(mlRowConverter);

        AddPredictionFieldForEstimate addPreOperator =
                new AddPredictionFieldForEstimate("addPreOperator", 1);

        Stream addPreStream = mleStream.applyOperator(addPreOperator);

        OnlineARIMAOperator onlineARIMAOperator =
                new OnlineARIMAOperator("onlineARIMAOperator", 1,2, 1, 1, 10, 0.01);

        addPreStream.applyOperator(onlineARIMAOperator);

        JobStarter starter = new JobStarter(job);
        starter.start();
    }
}
