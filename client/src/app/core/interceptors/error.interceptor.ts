import { HttpErrorResponse, HttpInterceptorFn, HttpResponse } from '@angular/common/http';
import { catchError, of, tap, throwError } from 'rxjs';
import { toAppError } from '../api/app-error';
import { ErrorResponse } from '../models/dtos';

/**
 * A 304 never carries a body, so passing it through with an empty body would
 * just trade a spurious error for a spurious empty list/object. This keeps
 * the last successful GET body per URL so a 304 can be resolved with the
 * data it's actually confirming is still valid, instead of nothing.
 *
 * sessionStorage, not a plain in-memory Map: the browser's HTTP cache (the
 * thing that causes the 304 in the first place) survives a page reload, so
 * our cache of bodies has to survive the reload too, or every reload after
 * the first would revalidate to a 304 with no in-memory body to fall back
 * on. sessionStorage is cleared when the tab closes, same rough lifetime as
 * the browser's revalidation cache for a session-scoped SPA like this one.
 */
const CACHE_PREFIX = 'resiaiac.lastGetBody:';

function readCachedBody(url: string): unknown {
  try {
    const raw = sessionStorage.getItem(CACHE_PREFIX + url);
    return raw === null ? undefined : JSON.parse(raw);
  } catch {
    return undefined;
  }
}

function writeCachedBody(url: string, body: unknown): void {
  try {
    sessionStorage.setItem(CACHE_PREFIX + url, JSON.stringify(body));
  } catch {
    // Storage full or unavailable (private browsing) — worst case a future
    // 304 for this URL falls back to an empty body, same as before this cache existed.
  }
}

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
    tap((event) => {
      if (req.method === 'GET' && event instanceof HttpResponse && event.status === 200) {
        writeCachedBody(req.urlWithParams, event.body);
      }
    }),
    catchError((err: unknown) => {
      if (err instanceof HttpErrorResponse) {
        if (err.status !== 0 && err.status < 400) {
          const body = err.status === 304 ? (readCachedBody(req.urlWithParams) ?? err.error) : err.error;

          return of(
            new HttpResponse({
              body,
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
