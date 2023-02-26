package space.gavinklfong.forex.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import space.gavinklfong.forex.api.dto.ApiErrorMessage;
import space.gavinklfong.forex.api.dto.ApiResponseErrorBody;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class RestExceptionHandler {

	@ExceptionHandler({InvalidRequestException.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ApiResponseErrorBody handleInvalidRequestException(InvalidRequestException ex) {
		return handlExceptionWithObjectErrors(ex.getErrors());
	}
	
	@ExceptionHandler({WebExchangeBindException.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ApiResponseErrorBody handleWebExchangeBindException(WebExchangeBindException ex) {
		return handlExceptionWithObjectErrors(ex.getAllErrors());
	}

	private ApiResponseErrorBody handlExceptionWithObjectErrors(List<ObjectError> errors) {
		List<ApiErrorMessage> errorMessages = errors.stream()
				.map(error -> {
					if (error instanceof FieldError) {
						FieldError fieldError = (FieldError) error;
						return new ApiErrorMessage().code(fieldError.getField()).message(fieldError.getDefaultMessage());
					} else {
						return new ApiErrorMessage().code(error.getCode()).message(error.getDefaultMessage());
					}
				})
				.collect(Collectors.toList());

		return new ApiResponseErrorBody().errors(errorMessages);
	}
	
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public ApiResponseErrorBody handleInternalServerError(Exception ex) {
		return new ApiResponseErrorBody().errors(List.of(new ApiErrorMessage().code("exception").message(ex.getMessage())));
	}

	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ResponseBody
	public ApiResponseErrorBody handleNotFound(Exception ex) {
		return new ApiResponseErrorBody().errors(List.of(new ApiErrorMessage().code("exception").message("Resource Not Found")));
	}

}
