import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

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
}

@Component({
  selector: 'app-sidenav',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './sidenav.component.html',
  styleUrl: './sidenav.component.scss'
})
export class SidenavComponent {
  isCollapsed = false;
  @Output() collapsed = new EventEmitter<boolean>();

  navItems: NavItem[] = [
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
    }
  ];

  toggleExpand(item: NavItem | NavChild): void {
    item.expanded = !item.expanded;
  }

  toggleSidenav(): void {
    this.isCollapsed = !this.isCollapsed;
    this.collapsed.emit(this.isCollapsed);
  }
}
