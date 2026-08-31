import { HttpErrorResponse, HttpInterceptorFn, HttpResponse } from '@angular/common/http';
import { catchError, of, throwError } from 'rxjs';
import { toAppError } from '../api/app-error';
import { ErrorResponse } from '../models/dtos';

/**
 * Unwraps the backend's ErrorResponse{status, message, timestamp} body into a
 * normalized AppError and rejects every failed request with that instead of
 * the raw HttpErrorResponse. See core/api/app-error.ts for how 4xx/5xx are
 * distinguished.
 *
 * Some responses in the 1xx/2xx/3xx range (304 Not Modified being the
 * common one here) aren't failures at all, but the browser's XHR layer still
 * reports them to Angular as an HttpErrorResponse, and they never carry the
 * backend's ErrorResponse body. Treat only real failures — 4xx/5xx from the
 * backend, or a genuine network/CORS failure (status 0) — as errors; resolve
 * everything else as a normal HttpResponse so callers aren't hit with a
 * spurious "Something went wrong." for a response that was never an error.
 */
export const errorInterceptor: HttpInterceptorFn = (req, next) =>
  next(req).pipe(
    catchError((err: unknown) => {
      if (err instanceof HttpErrorResponse) {
        if (err.status !== 0 && err.status < 400) {
          return of(
            new HttpResponse({
              body: err.error,
              headers: err.headers,
              status: err.status,
              statusText: err.statusText,
              url: err.url ?? undefined,
            }),
          );
        }

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
