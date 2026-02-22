import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

interface Tab {
  label: string;
  route: string;
}

@Component({
  selector: 'app-organization',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './organization.component.html',
  styleUrl: './organization.component.scss'
})
export class OrganizationComponent {
  tabs: Tab[] = [
    { label: 'Employees', route: 'employees' },
    { label: 'Projects', route: 'projects' }
  ];
}
