import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-auth-callback',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="callback-wrapper">
      <div class="callback-card">
        <div *ngIf="!errorMessage" class="loading">
          <div class="spinner"></div>
          <p>Completing sign in...</p>
        </div>
        <div *ngIf="errorMessage" class="error">
          <p class="error-text">{{ errorMessage }}</p>
          <button class="btn-back" (click)="goToLogin()">Back to Login</button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .callback-wrapper {
      display: flex;
      align-items: center;
      justify-content: center;
      min-height: 100vh;
      background-color: #f8f9fa;
    }
    .callback-card {
      text-align: center;
      padding: 40px;
    }
    .loading { display: flex; flex-direction: column; align-items: center; gap: 16px; }
    .loading p { font-size: 15px; color: #64748b; }
    .spinner {
      width: 32px; height: 32px;
      border: 3px solid #e2e8f0;
      border-top-color: #3b82f6;
      border-radius: 50%;
      animation: spin 0.6s linear infinite;
    }
    @keyframes spin { to { transform: rotate(360deg); } }
    .error-text { color: #dc2626; font-size: 14px; margin-bottom: 16px; }
    .btn-back {
      padding: 10px 24px;
      background: #3b82f6;
      color: #fff;
      border: none;
      border-radius: 6px;
      font-size: 14px;
      cursor: pointer;
    }
    .btn-back:hover { background: #2563eb; }
  `]
})
export class AuthCallbackComponent implements OnInit {
  errorMessage = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const code = this.route.snapshot.queryParamMap.get('code');
    const error = this.route.snapshot.queryParamMap.get('error');

    if (error) {
      this.errorMessage = 'SSO login was cancelled or failed. Please try again.';
      return;
    }

    if (!code) {
      this.errorMessage = 'No authorization code received. Please try again.';
      return;
    }

    this.authService.ssoCallback(code).subscribe({
      next: () => {
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.errorMessage = err.error?.message
          || 'SSO login failed. Your email may not be linked to an account.';
      }
    });
  }

  goToLogin(): void {
    this.router.navigate(['/login']);
  }
}
