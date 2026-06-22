package minecraftclone.logging;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Logger {
    private final static String NAME = "LOGGER";
    private static final BlockingQueue<LogEntry> entries = new LinkedBlockingQueue<>();
    private static final Path logPath = createLogPath();
    private static final Thread workerThread = new Thread(Logger::processEntries, "Logger");
    private static volatile boolean running = true;

    static {
        workerThread.setDaemon(true);
        workerThread.start();
        writeLog(new LogEntry(NAME, LogHeaderType.INFO, "Logger Initialized"));
    }

    private Logger() {
    }

    private static Path createLogPath()  {
        int iteration = 1;
        String logName = String.valueOf(LocalDate.now()) + "-";

        Path path = Paths.get("logs");
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        while (Files.exists(Paths.get("logs/" + logName + iteration + ".log"))) {
            iteration++;
        }

        return Paths.get("logs/" + logName + iteration + ".log");
    }

    private static void processEntries() {
        while (running || !entries.isEmpty()) {
            try {
                LogEntry entry = entries.poll(100, TimeUnit.MILLISECONDS);
                if (entry != null) {
                    writeLogToFile(entry);
                }
            } catch (InterruptedException e) {
                if (running) {
                    System.err.println("Logger thread interrupted: " + e.getMessage());
                }
            } catch (IOException e) {
                System.err.println("Failed to write log: " + e.getMessage());
            }
        }
    }

    private static void writeLogToFile(LogEntry entry) throws IOException {
        Files.writeString(logPath, entry.getFormatedTime() + " [" + entry.getHeader() + "] " + entry.getContents() + "\n", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    public static void writeLog(LogEntry entry) {
        entries.add(entry);
    }

    public static void writeLogs(ArrayList<LogEntry> entries) {
        for (LogEntry entry : entries) {
            writeLog(entry);
        }
    }

    public static void shutdown() {
        running = false;
        workerThread.interrupt();
        try {
            workerThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
