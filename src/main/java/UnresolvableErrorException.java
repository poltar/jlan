import java.lang.*;

public class UnresolvableErrorException extends Exception {
    public static final long serialVersionUID = 24L;

    //maybe add logging capabilities

    public UnresolvableErrorException() {
        super();
    }

    public UnresolvableErrorException(String error) {
        super(error);
    }

    public UnresolvableErrorException(Throwable cause, String error) {
        super(error, cause);
    }

    public UnresolvableErrorException(Throwable cause) {
        super(cause);
    }
}