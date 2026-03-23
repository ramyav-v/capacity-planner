import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../../auth/services/auth.service';

interface NavChild {
  label: string;
  route?: string;
  icon?: string;
  children?: NavChild[];
  expanded?: boolean;
}

interface NavItem {
  label: string;
  icon: string;
  route?: string;
  children?: NavChild[];
  expanded?: boolean;
  roles?: string[];
}

@Component({
  selector: 'app-sidenav',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './sidenav.component.html',
  styleUrl: './sidenav.component.scss'
})
export class SidenavComponent implements OnInit {
  isCollapsed = false;
  @Output() collapsed = new EventEmitter<boolean>();

  private allNavItems: NavItem[] = [
    {
      label: 'Dashboards',
      icon: 'dashboard',
      expanded: true,
      children: [
        { label: 'Overview', route: '/dashboard/overview' },
        { label: 'Allocations', route: '/dashboard/allocation' }
      ]
    },
    {
      label: 'Data Management',
      icon: 'storage',
      expanded: true,
      roles: ['ADMIN', 'SUPER_ADMIN'],
      children: [
        {
          label: 'Organization',
          expanded: true,
          children: [
            { label: 'Employees', route: '/data-management/organization/employees' },
            { label: 'Projects', route: '/data-management/organization/projects' }
          ]
        },
        { label: 'Capacity Inputs', route: '/data-management/capacity-inputs' }
      ]
    },
    {
      label: 'User Management',
      icon: 'people',
      route: '/user-management',
      roles: ['SUPER_ADMIN']
    }
  ];

  navItems: NavItem[] = [];

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    const user = this.authService.getCurrentUser();
    const userRole = user?.role ?? '';
    this.navItems = this.allNavItems.filter(
      item => !item.roles || item.roles.includes(userRole)
    );
  }

  toggleExpand(item: NavItem | NavChild): void {
    item.expanded = !item.expanded;
  }

  toggleSidenav(): void {
    this.isCollapsed = !this.isCollapsed;
    this.collapsed.emit(this.isCollapsed);
  }
}
