import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FinancialsService } from '../../services/financials.service';
import { ProjectFinancial, ProjectFinancialRole, ProjectResourcePlan, RoleCostConfig } from '../../financials.model';

@Component({
  selector: 'app-financials-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './financials-list.component.html',
  styleUrl: './financials-list.component.scss'
})
export class FinancialsListComponent implements OnInit {
  projects: ProjectFinancial[] = [];
  availableRoles: RoleCostConfig[] = [];

  expandedProjectId: number | null = null;
  loadingDetailId:   number | null = null;
  detailCache = new Map<number, ProjectFinancial>();

  // PS Fee inline edit
  editingPsFeeId: number | null = null;
  psFeeBuffer: number | null = null;

  // Resource plan new-row form state (keyed by projectId)
  newPlanRow: Record<number, Partial<ProjectResourcePlan>> = {};
  addingPlanForProject: number | null = null;

  constructor(private financialsService: FinancialsService) {}

  ngOnInit(): void {
    this.loadProjects();
    this.financialsService.getRoleCostConfigs().subscribe({
      next: (data) => this.availableRoles = data.sort((a, b) => a.role.localeCompare(b.role))
    });
  }

  loadProjects(): void {
    this.financialsService.getAllProjectFinancials().subscribe({
      next: (data) => this.projects = data,
      error: (err) => console.error('Failed to load financials', err)
    });
  }

  toggleProject(project: ProjectFinancial): void {
    if (this.expandedProjectId === project.projectId) {
      this.expandedProjectId = null;
      return;
    }
    this.expandedProjectId = project.projectId;
    this.loadDetail(project.projectId);
  }

  private loadDetail(projectId: number): void {
    this.loadingDetailId = projectId;
    this.financialsService.getProjectFinancial(projectId).subscribe({
      next: (detail) => {
        this.detailCache.set(projectId, detail);
        this.loadingDetailId = null;
      },
      error: () => { this.loadingDetailId = null; }
    });
  }

  getDetail(projectId: number): ProjectFinancial | undefined {
    return this.detailCache.get(projectId);
  }

  getNonDiscBreakdown(projectId: number): ProjectFinancialRole[] {
    return this.getDetail(projectId)?.nonDiscBreakdown ?? [];
  }

  getDiscBreakdown(projectId: number): ProjectFinancialRole[] {
    return this.getDetail(projectId)?.discBreakdown ?? [];
  }

  getResourcePlan(projectId: number): ProjectResourcePlan[] {
    return this.getDetail(projectId)?.resourcePlan ?? [];
  }

  getTotalHoursNonDisc(projectId: number): string {
    return this.getNonDiscBreakdown(projectId).reduce((s, r) => s + r.totalHours, 0).toFixed(0);
  }

  getTotalHoursDisc(projectId: number): string {
    return this.getDiscBreakdown(projectId).reduce((s, r) => s + r.totalHours, 0).toFixed(0);
  }

  // ── PS Fee ──────────────────────────────────────────────────────────────────

  startEditPsFee(project: ProjectFinancial, event: MouseEvent): void {
    event.stopPropagation();
    this.editingPsFeeId = project.projectId;
    this.psFeeBuffer = project.psFee;
  }

  savePsFee(project: ProjectFinancial, event: MouseEvent): void {
    event.stopPropagation();
    this.financialsService.updatePsFee(project.projectId, this.psFeeBuffer).subscribe({
      next: () => {
        this.editingPsFeeId = null;
        this.detailCache.delete(project.projectId);
        this.loadProjects();
        if (this.expandedProjectId === project.projectId) {
          this.loadDetail(project.projectId);
        }
      }
    });
  }

  cancelPsFeeEdit(event: MouseEvent): void {
    event.stopPropagation();
    this.editingPsFeeId = null;
    this.psFeeBuffer = null;
  }

  // ── Resource Plan ───────────────────────────────────────────────────────────

  startAddPlan(projectId: number): void {
    this.addingPlanForProject = projectId;
    this.newPlanRow[projectId] = { projectId, role: '', totalHours: 0 };
  }

  cancelAddPlan(projectId: number): void {
    this.addingPlanForProject = null;
    delete this.newPlanRow[projectId];
  }

  saveNewPlan(projectId: number): void {
    const row = this.newPlanRow[projectId];
    if (!row?.role || !row.totalHours || row.totalHours <= 0) return;

    this.financialsService.saveResourcePlan(projectId, {
      role: row.role!,
      totalHours: Number(row.totalHours)
    }).subscribe({
      next: () => {
        this.cancelAddPlan(projectId);
        this.detailCache.delete(projectId);
        this.loadDetail(projectId);
        this.loadProjects();
      }
    });
  }

  deletePlan(planId: number, projectId: number): void {
    this.financialsService.deleteResourcePlan(planId).subscribe({
      next: () => {
        this.detailCache.delete(projectId);
        this.loadDetail(projectId);
        this.loadProjects();
      }
    });
  }

  // ── Formatters ──────────────────────────────────────────────────────────────

  fc(value: number | null | undefined): string {
    if (value == null) return '—';
    return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD', maximumFractionDigits: 0 }).format(value);
  }

  fmPct(pct: number | null): string {
    if (pct == null) return '—';
    return (pct >= 0 ? '+' : '') + pct.toFixed(2) + '%';
  }

  marginClass(m: number | null): string {
    if (m == null) return '';
    return m >= 0 ? 'pos' : 'neg';
  }
}
