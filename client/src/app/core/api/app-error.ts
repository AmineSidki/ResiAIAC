/**
 * Normalized shape every failed HTTP call rejects with, after
 * HttpErrorInterceptor unwraps the backend's ErrorResponse{status, message,
 * timestamp}.
 *
 * `presentation` distinguishes "expected, user-facing" cases from opaque
 * server failures:
 *  - every 4xx the backend returns already carries a real, meaningful
 *    message (validation errors, RoomFullException, ResourceOwnershipMismatchException,
 *    ResourceNotFoundException) — safe and useful to show inline/in a toast
 *    as-is.
 *  - 5xx always comes back as the backend's generic "Something went wrong."
 *    (GlobalExceptionHandler's catch-all) — treat as opaque, non-actionable.
 *
 * Note: RoomFullException (400) and ResourceOwnershipMismatchException (404)
 * share their HTTP status with other exceptions (MethodArgumentNotValidException
 * also 400, ResourceNotFoundException also 404) — the wire shape gives no
 * further discriminator. Call sites in the two flows that can actually throw
 * them (reservation self-service create, self-service ownership-checked
 * GETs) should treat their 400/404 as this expected-message case rather than
 * guessing from the message text.
 */
export interface AppError {
  status: number;
  message: string;
  timestamp: string;
  presentation: 'expected' | 'generic';
}

export function toAppError(status: number, message: string, timestamp: string): AppError {
  return {
    status,
    message,
    timestamp,
    presentation: status >= 500 ? 'generic' : 'expected',
  };
}
