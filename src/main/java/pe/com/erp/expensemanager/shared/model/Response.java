package pe.com.erp.expensemanager.shared.model;

import java.io.Serializable;

public class Response implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String title;
	
	private String status;
	
	private String message;
	
	private String orden;
	
	private Object object;
	
	
	public String getOrden() {
		return orden;
	}

	public void setOrden(String orden) {
		this.orden = orden;
	}
	
	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	

	public Response() {

	}
	public Response(String title, String status, String message, String orden, Object object) {
		super();
		this.title = title;
		this.status = status;
		this.message = message;
		this.orden = orden;
		this.object = object;
	}

	@Override
	public String toString() {
		return "Response [title=" + title + ", status=" + status + ", message=" + message + ", orden=" + orden
				+ ", object=" + object + "]";
	}

	
	
}
