package api;

import lombok.Getter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvReaderSource extends Source {
    private final String directoryPath;  // CSV文件目录路径
    private final char delimiter;        // CSV字段分隔符
    private transient List<File> filesToProcess; // 该实例需要处理的文件列表
    private transient BufferedReader currentReader;
    private transient int currentFileIndex;

    /**
     * 构造函数
     * @param name        算子名称
     * @param parallelism 并行度
     * @param directory   监控的目录路径
     * @param delimiter   CSV字段分隔符
     */
    public CsvReaderSource(String name, int parallelism, String directory, char delimiter) {
        super(name, parallelism);
        this.directoryPath = directory;
        this.delimiter = delimiter;
    }

    @Override
    public void setupInstance(int instance) {
        File dir = new File(directoryPath);
        if (!dir.isDirectory()) {
            throw new RuntimeException("Invalid directory: " + directoryPath);
        }

        // 获取目录下所有CSV文件
        File[] allFiles = dir.listFiles((d, name) -> name.endsWith(".csv"));
        if (allFiles == null || allFiles.length == 0) {
            throw new RuntimeException("No CSV files found in directory: " + directoryPath);
        }

        // 根据并行度和实例ID分配文件
        this.filesToProcess = new ArrayList<>();
        for (int i = 0; i < allFiles.length; i++) {
            if (i % getParallelism() == instance) {
                filesToProcess.add(allFiles[i]);
            }
        }

        this.currentFileIndex = 0;
        openNextFile();
    }

    @Override
    public void getEvents(EventCollector eventCollector) {
        try {
            String line;
            while (true) {
                // 1. 从当前文件读取一行
                if ((line = currentReader.readLine()) != null) {
                    // 2. 解析CSV行（简单实现，实际应处理转义和引号）
                    String[] fields = line.split(String.valueOf(delimiter));

                    // 3. 创建事件并发送
//                    eventCollector.add(new CSVRecordEvent(fields));
                }
                // 4. 文件读取完毕时切换下一个文件
                else if (!openNextFile()) {
                    break;  // 没有更多文件可处理
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading CSV file", e);
        }
    }

    /**
     * 打开下一个待处理文件
     */
    private boolean openNextFile() {
        try {
            // 关闭当前文件（如果有）
            if (currentReader != null) {
                currentReader.close();
            }

            // 检查是否还有未处理的文件
            if (currentFileIndex >= filesToProcess.size()) {
                return false;
            }

            // 打开新文件
            File nextFile = filesToProcess.get(currentFileIndex++);
            currentReader = new BufferedReader(new FileReader(nextFile));
            return true;
        } catch (IOException e) {
            throw new RuntimeException("Failed to open file: " +
                    filesToProcess.get(currentFileIndex-1), e);
        }
    }
}
