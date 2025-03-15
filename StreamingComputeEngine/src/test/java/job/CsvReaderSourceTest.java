package job;

import api.CsvReaderSource;
import api.Job;
import api.Stream;
import engine.JobStarter;

public class CsvReaderSourceTest {
    public static void main(String[] args) {
        Job job = new Job("CsvReaderSourceTest_Job");
        String directory = "/Users/rain/Project/java/StreamingMachineLearning/StreamingComputeEngine/src/main/resources/data";

        CsvReaderSource csvReaderSource = new CsvReaderSource(
                "CsvReader",
                1,
                directory,
                ',');
        Stream csvStream = job.addSource(csvReaderSource);

        JobStarter starter = new JobStarter(job);
        starter.start();
    }
}
