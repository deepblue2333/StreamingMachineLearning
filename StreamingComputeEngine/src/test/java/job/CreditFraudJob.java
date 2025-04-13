package job;

import api.*;
import engine.JobStarter;

import java.util.HashMap;
import java.util.Map;

public class CreditFraudJob {
    public static void main(String[] args) {
        api.Job job = new api.Job("CreditFraudJob");
        String directory = "/Users/rain/Project/java/StreamingMachineLearning/StreamingComputeEngine/src/main/resources/data1";

        CsvReaderSource csvReaderSource = new CsvReaderSource(
                "CsvReader",
                1,
                directory,
                ',',
                new TableRowEvent.DataType[]{
                        TableRowEvent.DataType.INT,
                        TableRowEvent.DataType.DOUBLE,
                        TableRowEvent.DataType.DOUBLE,
                        TableRowEvent.DataType.DOUBLE,
                        TableRowEvent.DataType.DOUBLE,
                        TableRowEvent.DataType.DOUBLE,
                        TableRowEvent.DataType.DOUBLE,
                        TableRowEvent.DataType.DOUBLE,
                        TableRowEvent.DataType.DOUBLE,
                        TableRowEvent.DataType.DOUBLE,
                        TableRowEvent.DataType.DOUBLE,
                        TableRowEvent.DataType.DOUBLE,
                        TableRowEvent.DataType.DOUBLE,
                        TableRowEvent.DataType.DOUBLE,
                        TableRowEvent.DataType.DOUBLE,
                        TableRowEvent.DataType.DOUBLE,
                        TableRowEvent.DataType.DOUBLE,
                        TableRowEvent.DataType.DOUBLE,
                        TableRowEvent.DataType.DOUBLE,
                        TableRowEvent.DataType.DOUBLE,
                        TableRowEvent.DataType.DOUBLE,
                        TableRowEvent.DataType.DOUBLE,
                        TableRowEvent.DataType.DOUBLE,
                        TableRowEvent.DataType.DOUBLE,
                        TableRowEvent.DataType.DOUBLE,
                        TableRowEvent.DataType.DOUBLE,
                        TableRowEvent.DataType.DOUBLE,
                        TableRowEvent.DataType.DOUBLE,
                        TableRowEvent.DataType.DOUBLE,
                        TableRowEvent.DataType.DOUBLE,
                        TableRowEvent.DataType.STRING
                },
                new String[]{"Time", "V1", "V2", "V3", "V4", "V5", "V6", "V7", "V8", "V9", "V10", "V11", "V12", "V13", "V14", "V15", "V16", "V17", "V18", "V19", "V20", "V21", "V22", "V23", "V24", "V25", "V26", "V27", "V28", "Amount", "Class"},
                true);
        Stream csvStream = job.addSource(csvReaderSource);

        TransMap trans = new TransMap("trans", 1, new ReplicateGrouping());
        Stream stream_a_trans = csvStream.applyOperator(trans);

        Map<String, MachineLearningRowEvent.FieldType> fieldTypes = new HashMap<>();
//        fieldTypes.put("Timestamp", MachineLearningRowEvent.FieldType.FEATURE);
        fieldTypes.put("V1", MachineLearningRowEvent.FieldType.FEATURE);
        fieldTypes.put("V2", MachineLearningRowEvent.FieldType.FEATURE);
        fieldTypes.put("V3", MachineLearningRowEvent.FieldType.FEATURE);
        fieldTypes.put("predict_class", MachineLearningRowEvent.FieldType.PREDICTION);
        fieldTypes.put("trans_class", MachineLearningRowEvent.FieldType.GROUND_TRUTH);

        MachineLearningRowConvertOperator mlRowConverter =
                new MachineLearningRowConvertOperator("mlRowConverter", 1, fieldTypes);
        Stream stream_a_ml = stream_a_trans.applyOperator(mlRowConverter);

        PositiveFilter positiveFilter = new PositiveFilter("PositiveFilter", 1, new ReplicateGrouping());
        Stream positiveStream = stream_a_ml.applyOperator(positiveFilter);

        OlineBinaryClassifierOperator binaryClassifier = new OlineBinaryClassifierOperator("binaryClassifier", 2, 0.01);
        Stream binaryStream = positiveStream.applyOperator(binaryClassifier);

        stream_a_ml.applyOperator(binaryClassifier);

        CsvSink csv_sink = new CsvSink("CsvSink", 2, "TestSinkData");
        binaryStream.applyOperator(csv_sink);

//        PrintSink sink2 = new PrintSink("sink2", 2);
//        binaryStream.applyOperator(sink2);

//        PrintSink sink = new PrintSink("sink1", 2);
//        binaryStream.applyOperator(sink);

        JobStarter starter = new JobStarter(job);
        starter.start();

    }
}