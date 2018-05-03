import java.lang.*;

public class FileIsDirectoryException extends Exception {
	private static final long serialVersionUID = 32L;

	public FileIsDirectoryException() {
		super();
	}

	public FileIsDirectoryException(String error) {
		super(error);
	}

	public FileIsDirectoryException(Throwable cause, String error) {
		super(error, cause);
	}

	public FileIsDirectoryException(Throwable cause) {
		super(cause);
	}
}
