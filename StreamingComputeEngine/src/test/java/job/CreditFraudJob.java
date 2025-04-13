package job;

import api.*;
import engine.JobStarter;

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
        Stream csvStream2 = csvStream.applyOperator(trans);

        PositiveFilter positiveFilter = new PositiveFilter("PositiveFilter", 1, new ReplicateGrouping());
        Stream positiveStream = csvStream2.applyOperator(positiveFilter);

        PrintSink sink2 = new PrintSink("sink2", 2);
        positiveStream.applyOperator(sink2);

        PrintSink sink = new PrintSink("sink1", 2);
        csvStream2.applyOperator(sink);

        JobStarter starter = new JobStarter(job);
        starter.start();

    }
}