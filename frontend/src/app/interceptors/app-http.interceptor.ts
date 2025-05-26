import { HttpInterceptorFn, HttpRequest, HttpHandlerFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const appHttpInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const jwtToken = authService.jwtToken;

  // Exclude public endpoints from Authorization header
  const publicEndpoints = ['/auth/login', '/auth/register', '/auth/profile'];
  if (!publicEndpoints.some(url => req.url.includes(url))) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${jwtToken}`
      }
    });
  }
  return next(req);
};
