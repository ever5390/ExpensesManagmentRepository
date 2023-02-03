package pe.com.erp.expensemanager.exception;

public class CustomException extends RuntimeException{

	private static final long serialVersionUID = 5886884738744033807L;

	public CustomException(String message) {
		super(message);
	}
}
