package api;

import job.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class CsvSink extends Operator {
    private int instance;
    private String FilePath;

    public CsvSink(String name, int parallelism, String csvFile) {
        super(name, parallelism);
        this.FilePath = csvFile;
    }

    @Override
    public void setupInstance(int instance) {
        this.instance = instance;
    }

    @Override
    public void apply(Event event, EventCollector eventCollector) {
        String Instance_FilePath = String.format("%s_%s.csv", FilePath, instance);
        try (FileWriter writer = new FileWriter(Instance_FilePath, true)) {
            TableRowEvent rowEvent = (TableRowEvent) event;
            writer.write(rowEvent.toCSVLine() + "\n");
            Logger.log(String.format("Instance #%d write: %s", instance, rowEvent.toCSVLine()));
        } catch (IOException e) {
            System.out.println("写入CSV文件时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
