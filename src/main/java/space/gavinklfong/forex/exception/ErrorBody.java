package space.gavinklfong.forex.exception;

import java.util.ArrayList;
import java.util.List;

public class ErrorBody {

	private List<ErrorMessage> errors = new ArrayList<>();
	
	public ErrorBody() {
		super();
	}
	
	public ErrorBody(List<ErrorMessage> errors) {
		this.errors = errors;
	}

	public List<ErrorMessage> getErrors() {
		return errors;
	}

	public void setErrors(List<ErrorMessage> errors) {
		this.errors = errors;
	}
}

