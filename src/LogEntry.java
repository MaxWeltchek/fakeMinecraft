public class LogEntry {
    private final long time;
    private final String header;
    private final String contents;

    public LogEntry(String header_, String contents_) {
        time = System.currentTimeMillis();
        header = header_;
        contents = contents_;
    }

    public long getTime() {
        return time;
    }

    public String getFormatedTime() {
        long totalSeconds = time / 1000;
        long minutes = (totalSeconds / 60) % 60;
        long seconds = totalSeconds % 60;
        long hours = ((totalSeconds / 3600) % 24) - 7;

        return String.format("[%02d:%02d:%02d]", hours, minutes, seconds);
    }

    public String getHeader() {
        return header;
    }

    public String getContents() {
        return contents;
    }
}
