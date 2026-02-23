import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { NotificationService } from '../../shared/services/notification.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const notification = inject(NotificationService);

  if (req.url.includes('/auth/login')) {
    return next(req);
  }

  const token = authService.getToken();

  if (token) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        authService.logout();
      } else if (error.status === 403) {
        notification.show('You do not have permission to perform this action', 'error');
      } else if (error.status === 0) {
        notification.show('Unable to connect to server. Please check your network.', 'error');
      } else if (error.status >= 500) {
        notification.show('Something went wrong. Please try again later.', 'error');
      } else if (error.status === 404) {
        notification.show('The requested resource was not found.', 'error');
      } else if (error.status >= 400) {
        const message = error.error?.message || 'Request failed. Please check your input.';
        notification.show(message, 'error');
      }
      return throwError(() => error);
    })
  );
};
