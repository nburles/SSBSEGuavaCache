package energyProfiler;

public class FailedToCompileException extends Exception {
	  public FailedToCompileException() { super(); }
	  public FailedToCompileException(String message) { super(message); }
	  public FailedToCompileException(String message, Throwable cause) { super(message, cause); }
	  public FailedToCompileException(Throwable cause) { super(cause); }
}
