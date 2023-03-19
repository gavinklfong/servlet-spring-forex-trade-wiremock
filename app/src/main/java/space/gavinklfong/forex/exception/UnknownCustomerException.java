package space.gavinklfong.forex.exception;

public class UnknownCustomerException extends InvalidRequestException {

	public UnknownCustomerException() {
		super("customerId", "Unknown customer");
		
	}
	
}
