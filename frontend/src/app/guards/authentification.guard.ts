import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { inject } from '@angular/core';

export const authentificationGuard: CanActivateFn = (route, state) => {
  let authService = inject(AuthService);
  let router = inject(Router);

  if (!authService.isAuthenticated) {
    router.navigateByUrl('/login');
    return false;
  } 
  return true;
};
