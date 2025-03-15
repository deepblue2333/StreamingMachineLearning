package job;

public class Logger {
    public static synchronized void log(String message) {
        System.out.print(message);
    }
}
