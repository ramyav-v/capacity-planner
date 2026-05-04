import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ProjectFinancial, ProjectResourcePlan, RoleCostConfig } from '../financials.model';

@Injectable({ providedIn: 'root' })
export class FinancialsService {
  private readonly apiUrl = `${environment.CAPACITY_PLANNER_API}/financials`;

  constructor(private http: HttpClient) {}

  getAllProjectFinancials(): Observable<ProjectFinancial[]> {
    return this.http.get<ProjectFinancial[]>(`${this.apiUrl}/projects`);
  }

  getProjectFinancial(projectId: number): Observable<ProjectFinancial> {
    return this.http.get<ProjectFinancial>(`${this.apiUrl}/projects/${projectId}`);
  }

  updatePsFee(projectId: number, psFee: number | null): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/projects/${projectId}/ps-fee`, { psFee });
  }

  getRoleCostConfigs(): Observable<RoleCostConfig[]> {
    return this.http.get<RoleCostConfig[]>(`${this.apiUrl}/role-rates`);
  }

  saveRoleCostConfig(config: RoleCostConfig): Observable<RoleCostConfig> {
    return this.http.post<RoleCostConfig>(`${this.apiUrl}/role-rates`, config);
  }

  getResourcePlan(projectId: number): Observable<ProjectResourcePlan[]> {
    return this.http.get<ProjectResourcePlan[]>(`${this.apiUrl}/projects/${projectId}/resource-plan`);
  }

  saveResourcePlan(projectId: number, plan: Omit<ProjectResourcePlan, 'id' | 'projectId'>): Observable<ProjectResourcePlan> {
    return this.http.post<ProjectResourcePlan>(`${this.apiUrl}/projects/${projectId}/resource-plan`, plan);
  }

  deleteResourcePlan(planId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/resource-plan/${planId}`);
  }
}
