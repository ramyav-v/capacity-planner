import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DashboardService } from '../../services/dashboard.service';
import {
  ResourceAllocationDetail,
  ProjectSummary,
} from '../../dashboard.model';

export interface WeekEntry {
  label: string;
  date: string;
  isoDate: string;
  weekNumber: number;
}

@Component({
  selector: 'app-allocation',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './allocation.component.html',
  styleUrl: './allocation.component.scss'
})
export class AllocationComponent implements OnInit {
  searchText = '';
  selectedWeek: number | null = null;
  selectedWeekDate: string | null = null;
  quarterLabel = '';
  activeTab: 'resource' | 'project' = 'resource';

  weeks: WeekEntry[] = [];
  resources: ResourceAllocationDetail[] = [];
  projects: ProjectSummary[] = [];
  projectResources: ResourceAllocationDetail[] = [];
  selectedProjectId: number | null = null;

  constructor(private dashboardService: DashboardService) {}

  ngOnInit(): void {
    this.generateQuarterWeeks();
    this.loadResourceAllocations();
    this.loadProjects();
  }

  private generateQuarterWeeks(): void {
    const now = new Date();
    const q = Math.floor(now.getMonth() / 3) + 1;
    const year = now.getFullYear();
    this.quarterLabel = `Q${q} ${year}`;

    const startMonth = (q - 1) * 3;
    const quarterStart = new Date(year, startMonth, 1);

    // Find the Monday on or before the quarter start
    let weekStart = new Date(quarterStart);
    const day = weekStart.getDay();
    const diff = day === 0 ? 6 : day - 1;
    weekStart.setDate(weekStart.getDate() - diff);

    const quarterEnd = new Date(year, startMonth + 3, 0);
    const weeks: WeekEntry[] = [];
    let weekNum = 1;

    while (weekStart <= quarterEnd) {
      const month = weekStart.toLocaleString('en-US', { month: 'short' });
      const dayNum = weekStart.getDate();
      const iso = `${weekStart.getFullYear()}-${String(weekStart.getMonth() + 1).padStart(2, '0')}-${String(weekStart.getDate()).padStart(2, '0')}`;

      weeks.push({
        label: `Week ${weekNum}`,
        date: `${month} ${dayNum}`,
        isoDate: iso,
        weekNumber: weekNum
      });

      weekStart.setDate(weekStart.getDate() + 7);
      weekNum++;
    }

    this.weeks = weeks;
  }

  private loadResourceAllocations(weekStartDate?: string): void {
    this.dashboardService.getResourceAllocations(weekStartDate).subscribe({
      next: (data) => {
        this.resources = data.resources;
        if (!this.quarterLabel && data.quarter) {
          this.quarterLabel = data.quarter;
        }
      },
      error: (err) => console.error('Failed to load resource allocations', err)
    });
  }

  private loadProjects(): void {
    this.dashboardService.getActiveProjects().subscribe({
      next: (projects) => {
        this.projects = projects;
        if (projects.length > 0 && !this.selectedProjectId) {
          this.selectedProjectId = projects[0].id;
          this.loadProjectAllocations();
        }
      },
      error: (err) => console.error('Failed to load projects', err)
    });
  }

  private loadProjectAllocations(): void {
    if (!this.selectedProjectId) return;
    this.dashboardService.getProjectAllocations(
      this.selectedProjectId,
      this.selectedWeekDate || undefined
    ).subscribe({
      next: (data) => {
        this.projectResources = data.resources;
      },
      error: (err) => console.error('Failed to load project allocations', err)
    });
  }

  get filteredResources(): ResourceAllocationDetail[] {
    const list = this.activeTab === 'resource' ? this.resources : this.projectResources;
    if (!this.searchText.trim()) return list;
    const q = this.searchText.toLowerCase();
    return list.filter(
      r => r.name.toLowerCase().includes(q) || r.role.toLowerCase().includes(q)
    );
  }

  get selectedProject(): ProjectSummary | undefined {
    return this.projects.find(p => p.id === this.selectedProjectId);
  }

  getBarWidth(utilization: number): number {
    return Math.min(utilization, 110);
  }

  getBarColor(utilization: number): string {
    return '#2e2791';
  }

  getStatus(utilization: number): string {
    if (utilization === 0) return 'Available';
    if (utilization > 100) return 'Overloaded';
    if (utilization <= 50) return 'Partial';
    return 'Allocated';
  }

  getStatusClass(utilization: number): string {
    if (utilization === 0) return 'status-available';
    if (utilization > 100) return 'status-overloaded';
    if (utilization <= 50) return 'status-partial';
    return 'status-allocated';
  }

  selectQuarterView(): void {
    this.selectedWeek = null;
    this.selectedWeekDate = null;
    this.loadResourceAllocations();
    if (this.activeTab === 'project') {
      this.loadProjectAllocations();
    }
  }

  selectWeek(week: WeekEntry): void {
    if (this.selectedWeek === week.weekNumber) {
      // Deselect = back to quarter view
      this.selectedWeek = null;
      this.selectedWeekDate = null;
    } else {
      this.selectedWeek = week.weekNumber;
      this.selectedWeekDate = week.isoDate;
    }
    this.loadResourceAllocations(this.selectedWeekDate || undefined);
    if (this.activeTab === 'project') {
      this.loadProjectAllocations();

    }
  }

  selectProject(projectId: number): void {
    this.selectedProjectId = projectId;
    this.searchText = '';
    this.loadProjectAllocations();
  }

  switchTab(tab: 'resource' | 'project'): void {
    this.activeTab = tab;
    this.searchText = '';
    if (tab === 'project') {
      this.loadProjectAllocations();
    }
  }

  get mainTitle(): string {
    return this.activeTab === 'resource' ? 'Resource Allocations' : 'Project Allocations';
  }

  exportData(): void {
    const data = this.filteredResources;
    const header = 'Resource,Role,Region,Utilization,Status';
    const rows = data.map(
      r => `${r.name},${r.role},${r.region},${r.utilization}%,${r.status}`
    );
    const csv = [header, ...rows].join('\n');
    const blob = new Blob([csv], { type: 'text/csv' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `${this.activeTab}-allocations.csv`;
    a.click();
    URL.revokeObjectURL(url);
  }
}
