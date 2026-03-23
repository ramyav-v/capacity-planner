import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const roleGuard: CanActivateFn = (route) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const allowedRoles: string[] = route.data?.['roles'] ?? [];
  const user = authService.getCurrentUser();

  if (user && allowedRoles.includes(user.role)) {
    return true;
  }

  router.navigate(['/dashboard']);
  return false;
};
