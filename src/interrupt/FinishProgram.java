package interrupt;

public class FinishProgram extends RuntimeException {
    public FinishProgram(String message) {
        super(message);
    }
}
