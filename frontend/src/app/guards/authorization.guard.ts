import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { inject } from '@angular/core';

export const authorizationGuard: CanActivateFn = (route, state) => {
  let authService = inject(AuthService);
  let router = inject(Router);
  if (!authService.roles.includes('ROLE_ADMIN')) {
    router.navigateByUrl('/not-authorized');
    return false;
  }
  return true;
};
