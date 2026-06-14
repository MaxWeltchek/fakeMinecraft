package minecraftclone.input;

public class InvalidCommand extends RuntimeException {
    public InvalidCommand(String message) {
        super(message);
    }
    public InvalidCommand() {
        super("Unknown command");
    }
}
