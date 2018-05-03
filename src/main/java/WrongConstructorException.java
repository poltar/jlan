import java.lang.*;

public class WrongConstructorException extends Exception {
	private static final long serialVersionUID = 53L;

	public WrongConstructorException() {
        super();
    }

    public WrongConstructorException(String error) {
        super(error);
    }

    public WrongConstructorException(Throwable cause, String error) {
        super(error, cause);
    }

    public WrongConstructorException(Throwable cause) {
        super(cause);
    }
}
