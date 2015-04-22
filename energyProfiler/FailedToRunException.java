package energyProfiler;

public class FailedToRunException extends Exception {
	  public FailedToRunException() { super(); }
	  public FailedToRunException(String message) { super(message); }
	  public FailedToRunException(String message, Throwable cause) { super(message, cause); }
	  public FailedToRunException(Throwable cause) { super(cause); }
}
