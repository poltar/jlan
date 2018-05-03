import java.lang.*;

public class LocalhostException extends Exception {
    public static final long serialVersionUID = 32L;

    public LocalhostException() {
        super();
    }

    public LocalhostException(String error) {
        super(error);
    }

    public LocalhostException(Throwable cause, String error) {
        super(error, cause);
    }

    public LocalhostException(Throwable cause) {
        super(cause);
    }
}
