package academy.devdojo.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalErrorHandlerAdvice {

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<DefaultErrorMessage> handleNotFoundException(NotFoundException e) {
    var error = new DefaultErrorMessage(HttpStatus.NOT_FOUND.value(), e.getReason());

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
  public ResponseEntity<DefaultErrorMessage> handleSqlIntegrityViolation(SQLIntegrityConstraintViolationException e) {
    var error = new DefaultErrorMessage(HttpStatus.BAD_REQUEST.value(), "Duplicated entry for one of the unique fields");

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
    var defaultMessage = e.getBindingResult()
        .getAllErrors()
        .stream()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .filter(Objects::nonNull)
        .sorted()
        .collect(Collectors.joining(", "));

    var apiError = ApiError.builder()
        .timestamp(OffsetDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
        .status(HttpStatus.BAD_REQUEST.value())
        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
        .path(request.getRequestURI())
        .message(defaultMessage)
        .build();

    return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);

  }
}
