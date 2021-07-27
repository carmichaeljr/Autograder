package AutoGrader;

import java.lang.Exception;

public class AutoGraderException extends Exception {
	public AutoGraderException(String errorMessage){
		super(errorMessage);
	}
}
