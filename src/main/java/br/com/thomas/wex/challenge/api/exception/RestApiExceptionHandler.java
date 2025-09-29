package br.com.thomas.wex.challenge.api.exception;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import br.com.thomas.wex.challenge.api.controller.dto.response.ApiErrorDto;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

/**
 * @author Thomas J. Mazon de Oiveira
 */
@ControllerAdvice(basePackages = "br.com.thomas.wex")
public class RestApiExceptionHandler extends ResponseEntityExceptionHandler {


	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		Map<String, String> fieldErrors = new HashMap<>();

		if (null != ex.getBindingResult()) {
			if (null != ex.getBindingResult().getFieldErrors()) {
				
				Map<String, List<String>> map = ex.getBindingResult().getFieldErrors().stream()
			            .collect(
			                    Collectors.groupingBy(
			                    		FieldError::getField, Collectors.mapping(
			                    				FieldError::getDefaultMessage, Collectors.toList())));
				
				
				map.entrySet().forEach(entry -> {
					fieldErrors.put(entry.getKey(), entry.getValue().toString());
				});
			}

			if (null != ex.getBindingResult().getGlobalErrors()) {
				
				
				Map<String, List<String>> map = ex.getBindingResult().getFieldErrors().stream()
			            .collect(
			                    Collectors.groupingBy(
			                    		FieldError::getField, Collectors.mapping(
			                    				FieldError::getDefaultMessage, Collectors.toList())));
				
				
				map.entrySet().forEach(entry -> {
					fieldErrors.put(entry.getKey(), entry.getValue().toString());
				});
			}

		}

		ApiErrorDto apiError = new ApiErrorDto(HttpStatus.BAD_REQUEST.value(), ex.getLocalizedMessage(), fieldErrors);

		return new ResponseEntity<>(apiError, headers, HttpStatus.BAD_REQUEST);
	}

	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		final Throwable cause = ex.getCause() == null ? ex : ex.getCause();
		Map<String, String> fieldErrors = new HashMap<>();

		if (cause instanceof InvalidFormatException) {
			InvalidFormatException.class.cast(cause).getPath()
					.forEach(ref -> fieldErrors.put(ref.getFieldName(), cause.getLocalizedMessage()));
		}

		ApiErrorDto apiError = new ApiErrorDto(HttpStatus.BAD_REQUEST.value(), ex.getLocalizedMessage(), fieldErrors);

		return new ResponseEntity<>(apiError, headers, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({
			InvalidFormatException.class, ConstraintViolationException.class
	})
	public ResponseEntity<ApiErrorDto> handleInputNotReadableException(Exception ex) {

		Map<String, String> fieldErrors = new HashMap<>();

		final Throwable cause = ex.getCause() == null ? ex : ex.getCause();
		String detail = cause.getMessage();

		if (cause instanceof JsonMappingException) {
			JsonMappingException.class.cast(cause).getPath()
					.forEach(ref -> fieldErrors.put(ref.getFieldName(), cause.getLocalizedMessage()));
		} else if (ex instanceof ConstraintViolationException) {
			ConstraintViolationException cve = (ConstraintViolationException) ex;
			
			
			Map<Object, List<String>> map = cve.getConstraintViolations().stream()
		            .collect(
		                    Collectors.groupingBy(
		                    		ConstraintViolation<?>::getPropertyPath, Collectors.mapping(
		                    				ConstraintViolation::getMessage, Collectors.toList())));
			
			map.entrySet().forEach(entry -> {
				fieldErrors.put(entry.getKey().toString(), entry.getValue().toString());
			});
		}

		ApiErrorDto apiError = new ApiErrorDto(HttpStatus.BAD_REQUEST.value(), detail, fieldErrors);

		return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({
			ForbiddenException.class
	})
	public ResponseEntity<Object> handleAuthenticationException(Exception ex, Locale locale) {

		ApiErrorDto apiError = new ApiErrorDto(HttpStatus.FORBIDDEN.value(), ex.getMessage());
		return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler({
			AccessDeniedException.class
	})
	public ResponseEntity<Object> handleAccessDeniedException(Exception ex, Locale locale) {

		ApiErrorDto apiError = new ApiErrorDto(HttpStatus.UNAUTHORIZED.value(), "{error.auth.access-denied}");
		return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler({
			IllegalArgumentException.class, ArithmeticException.class
	})
	public ResponseEntity<ApiErrorDto> handleRestException(Exception ex, WebRequest request) {
		return mapException(ex, HttpStatus.BAD_REQUEST);
	}

	/**
	 * 
	 * @param ex
	 * @param request
	 * @param locale
	 * @return
	 */
	@ExceptionHandler(value = {
			MalformedJwtException.class, UnauthorizedException.class, BadCredentialsException.class,
			ExpiredJwtException.class
	})
	protected ResponseEntity<Object> handleUnauthorizedException(Exception ex, WebRequest request, Locale locale) {
		ApiErrorDto body = new ApiErrorDto(HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
	}

	/**
	 * Maps exceptions into expected payload by clients: {@link ApiErrorDto}
	 */
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
			request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
		}

		ApiErrorDto apiErrorDto = body instanceof ApiErrorDto ? ApiErrorDto.class.cast(body)
				: buildResponseBody(ex, status);

		return new ResponseEntity<>(apiErrorDto, headers, status);
	}

	/**
	 * Handle {@link NotFoundException}
	 * 
	 * @param ex
	 * @param request
	 * @param locale
	 * @return
	 */
	@ExceptionHandler(value = {
			NotFoundException.class
	})
	protected ResponseEntity<Object> handleNotFoundException(Exception ex, WebRequest request, Locale locale) {
		ApiErrorDto body = new ApiErrorDto(HttpStatus.NOT_FOUND.value(), ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
	}

	
	/**
	 * Build an {@link ApiErrorDto} from a given exception and status.
	 * 
	 * @param ex
	 * @param status
	 * @return
	 */
	private ApiErrorDto buildResponseBody(Throwable ex, HttpStatus status) {
		return new ApiErrorDto(status.value(), ex.getMessage());
	}

	/**
	 * Build a {@link ResponseEntity} from a given exception and status.
	 * 
	 * @param ex
	 * @param status
	 * @return
	 */
	private ResponseEntity<ApiErrorDto> mapException(Throwable ex, HttpStatus status) {
		return ResponseEntity.status(status).body(buildResponseBody(ex, status));
	}

}