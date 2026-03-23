import { Routes } from '@angular/router';
import { authGuard } from './auth/guards/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () =>
      import('./auth/login/login.component')
        .then(c => c.LoginComponent)
  },
  {
    path: 'auth/callback',
    loadComponent: () =>
      import('./auth/callback/auth-callback.component')
        .then(c => c.AuthCallbackComponent)
  },
  {
    path: '',
    canActivate: [authGuard],
    loadChildren: () =>
      import('./core-routing/core-routing-routing.module')
        .then(r => r.CORE_ROUTES)
  }
];

