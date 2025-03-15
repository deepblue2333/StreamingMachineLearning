package job;

import api.CsvReaderSource;
import api.Job;
import api.Stream;
import api.TableRowEvent;
import engine.JobStarter;

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

        JobStarter starter = new JobStarter(job);
        starter.start();
    }
}
