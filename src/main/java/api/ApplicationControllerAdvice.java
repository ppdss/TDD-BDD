package api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import api.exception.ApiErrors;
import api.exception.BusinessException;

@RestControllerAdvice
public class ApplicationControllerAdvice {



	
	/**
	 * Exception para parâmetros invalidos 
	 * @param MethodArgumentNotValidException: Exceção lançada quando se algum campo verificado pelo @Valid não passar no teste do validator
	 * @return ApiErrors(bindResult)
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)  
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiErrors handleValidationExceptions(MethodArgumentNotValidException ex) {
		BindingResult bindResult = ex.getBindingResult();
		return new ApiErrors(bindResult);
	}

	/**
	 * Exception para regra de negócio
	 * @param BusinessException
	 * @return ApiErrors(BusinessException ex)
	 */
	@ExceptionHandler(BusinessException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiErrors handleValidationExceptions(BusinessException ex) {
		return new ApiErrors(ex);
	}



	/**
	 * ResponseStatusException: Utilizado para lançar erros da nossa API 
	 * @param ResponseStatusException ex
	 * @return ResponseEntity<ApiErrors>(ApiErrors(ex), ex.getStatus()
	 */
	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ApiErrors> handleResponseStatusException(ResponseStatusException ex) {
		return new ResponseEntity<ApiErrors>(new ApiErrors(ex), ex.getStatus());
	}
}


