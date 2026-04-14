import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DashboardService } from '../../services/dashboard.service';
import { HiringGapByRole, HiringGapByProject } from '../../dashboard.model';

type ViewTab = 'role' | 'project';

@Component({
  selector: 'app-hiring-gap',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './hiring-gap.component.html',
  styleUrl: './hiring-gap.component.scss'
})
export class HiringGapComponent implements OnInit {
  activeTab: ViewTab = 'role';
  quarterLabel = '';

  // Date range filter
  startDate = '';
  endDate = '';
  isCustomRange = false;

  byRole: HiringGapByRole[] = [];
  byProject: HiringGapByProject[] = [];
  expandedProjectId: number | null = null;

  // Popup state (same pattern as overview)
  popupVisible = false;
  popupLabel = '';
  popupNames: string[] = [];

  constructor(private dashboardService: DashboardService) {}

  ngOnInit(): void {
    const now = new Date();
    const q = Math.floor(now.getMonth() / 3) + 1;
    this.quarterLabel = `Q${q} ${now.getFullYear()}`;
    this.loadHiringGap();
  }

  loadHiringGap(): void {
    const start = this.isCustomRange ? this.startDate : undefined;
    const end = this.isCustomRange ? this.endDate : undefined;
    this.dashboardService.getHiringGap(start, end).subscribe({
      next: (data) => {
        this.byRole = data.byRole;
        this.byProject = data.byProject;
        if (data.quarter) this.quarterLabel = data.quarter;
      },
      error: (err) => console.error('Failed to load hiring gap', err)
    });
  }

  switchTab(tab: ViewTab): void {
    this.activeTab = tab;
  }

  toggleCustomRange(enabled: boolean): void {
    this.isCustomRange = enabled;
    if (!enabled) {
      this.startDate = '';
      this.endDate = '';
      this.loadHiringGap();
    }
  }

  applyDateRange(): void {
    if (this.startDate && this.endDate) {
      this.loadHiringGap();
    }
  }

  toggleProject(projectId: number): void {
    this.expandedProjectId = this.expandedProjectId === projectId ? null : projectId;
  }

  showRolesPopup(project: HiringGapByProject, event: MouseEvent): void {
    event.stopPropagation();
    this.popupLabel = `${project.projectCode} — Roles`;
    this.popupNames = project.roleGaps.map(r => r.role);
    this.popupVisible = true;
  }

  closePopup(): void {
    this.popupVisible = false;
  }

  getGapClass(gap: number): string {
    if (gap < 0) return 'gap-negative';
    if (gap === 0) return 'gap-zero';
    return 'gap-positive';
  }

  getGapLabel(gap: number): string {
    if (gap < 0) return `${gap} FTE`;
    if (gap === 0) return '0 FTE';
    return `+${gap} FTE`;
  }

  get negativeGapCount(): number {
    return this.byRole.filter(r => r.gap < 0).length;
  }

  exportData(): void {
    const header = 'Role,Available FTE,Required FTE,FTE Gap';
    const rows = this.byRole.map(
      r => `${r.role},${r.availableFte},${r.requiredFte},${r.gap}`
    );
    const csv = [header, ...rows].join('\n');
    const blob = new Blob([csv], { type: 'text/csv' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'hiring-gap.csv';
    a.click();
    URL.revokeObjectURL(url);
  }
}
