package api;

import job.Logger;
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

    private final boolean skipHeader;
    private final TableRowEvent.DataType[] columnTypes;

    private final String[] columnNames;

//    public enum DataType {
//        STRING, INT, LONG, DOUBLE, BOOLEAN
//    }

    /**
     * 构造函数
     * @param name        算子名称
     * @param parallelism 并行度
     * @param directory   监控的目录路径
     * @param delimiter   CSV字段分隔符
     */
//    public CsvReaderSource(String name, int parallelism, String directory, char delimiter) {
//        super(name, parallelism);
//        this.directoryPath = directory;
//        this.delimiter = delimiter;
//    }

    /**
     * 完整参数构造函数
     * @param columnTypes  每列数据类型（数组长度需与CSV列数一致）
     * @param skipHeader   是否跳过首行头部
     */
    public CsvReaderSource(String name, int parallelism,
                           String directory, char delimiter,
                           TableRowEvent.DataType[] columnTypes, String[] columnNames, boolean skipHeader) {
        super(name, parallelism);
        this.directoryPath = directory;
        this.delimiter = delimiter;
        this.columnTypes = columnTypes;
        this.skipHeader = skipHeader;
        this.columnNames = columnNames;
    }

    // 简化构造函数（默认不跳过头）
    public CsvReaderSource(String name, int parallelism,
                           String directory, char delimiter,
                           TableRowEvent.DataType[] columnTypes, String[] columnNames) {
        this(name, parallelism, directory, delimiter, columnTypes,  columnNames, false);
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
//            while (true) {
                // 1. 从当前文件读取一行
                if ((line = currentReader.readLine()) != null) {
                    // 2. 解析CSV行（简单实现，实际应处理转义和引号）
//                    String[] fields = line.split(String.valueOf(delimiter));
                    TableRowEvent event = parseLine(line);

                     // 3. 创建事件并发送
                    eventCollector.add(event);

                    Logger.log(String.format("add event to eventCollector: %s \n", event));
                }
                // 4. 文件读取完毕时切换下一个文件
//                else if (!openNextFile()) {
//                    break;  // 没有更多文件可处理
//                }
//            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading CSV file", e);
        }
    }

    private TableRowEvent parseLine(String line) {
        TableRowEvent event = new TableRowEvent();

        String[] raw = line.split(String.valueOf(delimiter));
        Object[] result = new Object[raw.length];

        for (int i = 0; i < raw.length; i++) {
            try {
                result[i] = convertValue(raw[i].trim(), columnTypes[i]);
                event.addField(columnNames[i], columnTypes[i], result[i]);
            } catch (Exception e) {
                throw new CsvParseException(
                        String.format("Parse error at column %d: %s", i, raw[i]), e);
            }
        }
        return event;
    }

    private Object convertValue(String value, TableRowEvent.DataType type) {
        if (value.isEmpty()) return null;

        switch (type) {
            case STRING:
                return value;
            case INT:
                return Integer.parseInt(value);
//            case LONG:
//                return Long.parseLong(value);
            case DOUBLE:
                return Double.parseDouble(value);
//            case BOOLEAN:
//                return parseBoolean(value);
            default:
                throw new UnsupportedOperationException("Unsupported type: " + type);
        }
    }

    private boolean parseBoolean(String value) {
        if (value.equalsIgnoreCase("true")) return true;
        if (value.equalsIgnoreCase("false")) return false;
        if (value.equals("1")) return true;
        if (value.equals("0")) return false;
        throw new IllegalArgumentException("Invalid boolean value: " + value);
    }

    public static class CsvParseException extends RuntimeException {
        public CsvParseException(String message, Throwable cause) {
            super(message, cause);
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

            // 跳过头部
            if (skipHeader) {
                currentReader.readLine();
            }

            return true;
        } catch (IOException e) {
            throw new RuntimeException("Failed to open file: " +
                    filesToProcess.get(currentFileIndex-1), e);
        }
    }
}
