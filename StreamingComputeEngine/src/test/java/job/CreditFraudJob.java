package job;

import api.CsvReaderSource;
import api.PrintSink;
import api.Stream;
import api.TableRowEvent;

public class CreditFraudJob {
    public static void main(String[] args) {
        api.Job job = new api.Job("CreditFraudJob");
        String directory = "/Users/rain/Project/java/StreamingMachineLearning/StreamingComputeEngine/src/main/resources/data";

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
                        TableRowEvent.DataType.INT
                },
                new String[]{"Time", "V1", "V2", "V3", "V4", "V5", "V6", "V7", "V8", "V9", "V10", "V11", "V12", "V13", "V14", "V15", "V16", "V17", "V18", "V19", "V20", "V21", "V22", "V23", "V24", "V25", "V26", "V27", "V28", "Amount", "Class"},
                true);
        Stream csvStream = job.addSource(csvReaderSource);

        PrintSink sink = new PrintSink("sink1", 1);
        csvStream.applyOperator(sink);
    }
}