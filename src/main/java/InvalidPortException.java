import java.lang.*;

public class InvalidPortException extends Exception {
    public static final long serialVersionUID = 38L;

    public InvalidPortException() {
        super();
    }

    public InvalidPortException(String error) {
        super(error);
    }

    public InvalidPortException(Throwable cause, String error) {
        super(error, cause);
    }

    public InvalidPortException(Throwable cause) {
        super(cause);
    }
}