import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';
import { toAppError } from '../api/app-error';
import { ErrorResponse } from '../models/dtos';

/**
 * Unwraps the backend's ErrorResponse{status, message, timestamp} body into a
 * normalized AppError and rejects every failed request with that instead of
 * the raw HttpErrorResponse. See core/api/app-error.ts for how 4xx/5xx are
 * distinguished.
 */
export const errorInterceptor: HttpInterceptorFn = (req, next) =>
  next(req).pipe(
    catchError((err: unknown) => {
      if (err instanceof HttpErrorResponse) {
        const body = err.error as Partial<ErrorResponse> | null;

        if (body && typeof body.status === 'number' && typeof body.message === 'string') {
          return throwError(() =>
            toAppError(body.status!, body.message!, body.timestamp ?? new Date().toISOString()),
          );
        }

        // Network failure, CORS rejection, or a non-JSON error body (e.g.
        // the dev server isn't on :4200 and CORS silently drops it).
        return throwError(() =>
          toAppError(
            err.status || 0,
            err.status === 0
              ? 'Could not reach the server. Check your connection or that the API is running.'
              : 'Something went wrong.',
            new Date().toISOString(),
          ),
        );
      }

      return throwError(() => err);
    }),
  );
