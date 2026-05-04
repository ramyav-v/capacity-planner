import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../auth/services/auth.service';

@Component({
  selector: 'app-project-financials',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './project-financials.component.html',
  styleUrl: './project-financials.component.scss'
})
export class ProjectFinancialsComponent {
  tabs: { label: string; route: string }[];

  constructor(private authService: AuthService) {
    const role = this.authService.getCurrentUser()?.role ?? '';
    this.tabs = [{ label: 'Project Financials', route: 'overview' }];
    if (role === 'ADMIN' || role === 'SUPER_ADMIN') {
      this.tabs.push({ label: 'Role Rates', route: 'role-rates' });
    }
  }
}
