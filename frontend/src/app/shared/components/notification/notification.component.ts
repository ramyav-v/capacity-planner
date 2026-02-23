import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';
import { NotificationService, Notification } from '../../services/notification.service';

@Component({
  selector: 'app-notification',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="notification-container">
      <div
        *ngFor="let n of notifications; let i = index"
        class="notification"
        [ngClass]="'notification-' + n.type"
        (click)="dismiss(i)">
        <span class="notification-message">{{ n.message }}</span>
        <button class="notification-close">&times;</button>
      </div>
    </div>
  `,
  styles: [`
    .notification-container {
      position: fixed;
      top: 20px;
      right: 20px;
      z-index: 10000;
      display: flex;
      flex-direction: column;
      gap: 8px;
      max-width: 400px;
    }
    .notification {
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: 12px;
      padding: 12px 16px;
      border-radius: 8px;
      font-size: 13px;
      font-weight: 500;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      cursor: pointer;
      animation: slideIn 0.3s ease-out;
    }
    .notification-error {
      background: #ffebee;
      color: #c62828;
      border: 1px solid #ffcdd2;
    }
    .notification-success {
      background: #e8f5e9;
      color: #2e7d32;
      border: 1px solid #c8e6c9;
    }
    .notification-info {
      background: #e3f2fd;
      color: #1565c0;
      border: 1px solid #bbdefb;
    }
    .notification-message {
      flex: 1;
    }
    .notification-close {
      background: none;
      border: none;
      font-size: 18px;
      cursor: pointer;
      color: inherit;
      opacity: 0.7;
      padding: 0 2px;
    }
    .notification-close:hover {
      opacity: 1;
    }
    @keyframes slideIn {
      from { transform: translateX(100%); opacity: 0; }
      to { transform: translateX(0); opacity: 1; }
    }
  `]
})
export class NotificationComponent implements OnInit, OnDestroy {
  notifications: Notification[] = [];
  private subscription!: Subscription;

  constructor(private notificationService: NotificationService) {}

  ngOnInit(): void {
    this.subscription = this.notificationService.notification$.subscribe(notification => {
      this.notifications.push(notification);
      setTimeout(() => this.dismiss(0), 5000);
    });
  }

  dismiss(index: number): void {
    this.notifications.splice(index, 1);
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
  }
}
