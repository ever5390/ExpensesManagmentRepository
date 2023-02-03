package pe.com.erp.expensemanager.exception;

import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import pe.com.erp.expensemanager.shared.model.Response;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler{


	@Override
	protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		// TODO Auto-generated method stub
		Response response = new Response();
		response.setTitle("ERROR");
		response.setStatus("error");
		response.setMessage("El estado: "+ request.getParameter("status")+" no existe para la cuenta");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}


	
	
}
