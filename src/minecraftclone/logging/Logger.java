package minecraftclone.logging;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;

public class Logger {
    private Path logPath;

    public Logger()  {
        int iteration = 1;
        String logName = String.valueOf(LocalDate.now()) + "-";

        Path path = Paths.get("logs");
        File[] files = path.toFile().listFiles();
        while (Files.exists(Paths.get("logs/" + logName + iteration + ".log"))) {
            iteration++;
        }

        logPath = Paths.get("logs/" + logName + iteration + ".log");
        LogEntry initialEntry = new LogEntry("LOGGER/INFO", "Logger Initialized");

        try {
            writeLog(initialEntry);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeLog(LogEntry entry) throws IOException {
        Files.writeString(logPath, entry.getFormatedTime() + " [" + entry.getHeader() + "] " + entry.getContents() + "\n", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    public void writeLogs(ArrayList<LogEntry> entries) throws IOException {
        for (LogEntry entry : entries) {
            writeLog(entry);
        }
    }
}
