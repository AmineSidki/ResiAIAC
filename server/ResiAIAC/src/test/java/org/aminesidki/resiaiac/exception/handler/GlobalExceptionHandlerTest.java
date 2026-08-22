package org.aminesidki.resiaiac.exception.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.aminesidki.resiaiac.dto.response.ErrorResponse;
import org.aminesidki.resiaiac.exception.GlobalExceptionHandler;
import org.aminesidki.resiaiac.exception.ResourceNotFoundException;
import org.aminesidki.resiaiac.exception.ResourceOwnershipMismatchException;
import org.aminesidki.resiaiac.exception.RoomFullException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

/**
 * Unit tests for {@link GlobalExceptionHandler}, calling each {@code @ExceptionHandler} method
 * directly rather than through a {@code MockMvc}/web-layer slice — the mapping logic itself
 * (exception type -> status code) is what's under test here, independent of Spring's dispatch
 * machinery.
 */
class GlobalExceptionHandlerTest {

  private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

  @Test
  void handleResourceNotFound_shouldMapToNotFound() {
    ResourceNotFoundException ex = new ResourceNotFoundException("Reclamation not found");

    ResponseEntity<?> response = handler.handleResourceNotFound(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    ErrorResponse body = (ErrorResponse) response.getBody();
    assertThat(body).isNotNull();
    assertThat(body.status()).isEqualTo(HttpStatus.NOT_FOUND.value());
    assertThat(body.message()).isEqualTo("Reclamation not found");
  }

  @Test
  void handleMethodArgumentNotValidException_shouldMapToBadRequest() {
    MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
    BindingResult bindingResult = mock(BindingResult.class);
    FieldError fieldError = new FieldError("dto", "message", "must not be blank");
    when(ex.getBindingResult()).thenReturn(bindingResult);
    when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

    ResponseEntity<?> response = handler.handleMethodArgumentNotValidException(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    ErrorResponse body = (ErrorResponse) response.getBody();
    assertThat(body).isNotNull();
    assertThat(body.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(body.message()).contains("message");
  }

  @Test
  void handleRoomFullException_shouldMapToBadRequest() {
    RoomFullException ex = new RoomFullException("La chambre B1-101 est totalement occupee !");

    ResponseEntity<?> response = handler.handleRoomFullException(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    ErrorResponse body = (ErrorResponse) response.getBody();
    assertThat(body).isNotNull();
    assertThat(body.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(body.message()).isEqualTo("La chambre B1-101 est totalement occupee !");
  }

  @Test
  void handleResourceOwnershipMismatchException_shouldMapToNotFound() {
    ResourceOwnershipMismatchException ex =
        new ResourceOwnershipMismatchException(
            "Queried resource does not belong to querying user !");

    ResponseEntity<?> response = handler.handleResourceOwnershipMismatchException(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    ErrorResponse body = (ErrorResponse) response.getBody();
    assertThat(body).isNotNull();
    assertThat(body.status()).isEqualTo(HttpStatus.NOT_FOUND.value());
    assertThat(body.message()).isEqualTo("Queried resource does not belong to querying user !");
  }

  @Test
  void handleGenericException_shouldMapToInternalServerErrorAndHideMessageDetails() {
    RuntimeException ex = new RuntimeException("some internal detail that shouldn't leak");

    ResponseEntity<?> response = handler.handleGenericException(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    ErrorResponse body = (ErrorResponse) response.getBody();
    assertThat(body).isNotNull();
    assertThat(body.status()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    // The generic handler is intentionally opaque - it must not leak the original exception
    // message to the client.
    assertThat(body.message()).isEqualTo("Something went wrong.");
  }
}
