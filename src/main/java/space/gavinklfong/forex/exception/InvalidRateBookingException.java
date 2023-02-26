package space.gavinklfong.forex.exception;

public class InvalidRateBookingException extends InvalidRequestException {

	public InvalidRateBookingException() {
		super("RateBooking", "Invalid Rate Booking");
		
	}
}
