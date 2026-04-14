import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import {
  DashboardResponse,
  HiringGapResponse,
  ResourceAllocationResponse,
  ProjectSummary,
  ProjectAllocationResponse
} from '../dashboard.model';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private readonly apiUrl = `${environment.CAPACITY_PLANNER_API}/dashboard`;

  constructor(private http: HttpClient) {}

  getDashboard(): Observable<DashboardResponse> {
    return this.http.get<DashboardResponse>(this.apiUrl);
  }

  getResourceAllocations(weekStartDate?: string, employeeId?: number): Observable<ResourceAllocationResponse> {
    let params = new HttpParams();
    if (weekStartDate) params = params.set('weekStartDate', weekStartDate);
    if (employeeId) params = params.set('employeeId', employeeId.toString());
    return this.http.get<ResourceAllocationResponse>(`${this.apiUrl}/allocations`, { params });
  }

  getActiveProjects(): Observable<ProjectSummary[]> {
    return this.http.get<ProjectSummary[]>(`${this.apiUrl}/projects`);
  }

  getProjectAllocations(projectId: number, weekStartDate?: string, employeeId?: number): Observable<ProjectAllocationResponse> {
    let params = new HttpParams();
    if (weekStartDate) params = params.set('weekStartDate', weekStartDate);
    if (employeeId) params = params.set('employeeId', employeeId.toString());
    return this.http.get<ProjectAllocationResponse>(`${this.apiUrl}/projects/${projectId}/allocations`, { params });
  }

  getHiringGap(startDate?: string, endDate?: string): Observable<HiringGapResponse> {
    let params = new HttpParams();
    if (startDate) params = params.set('startDate', startDate);
    if (endDate) params = params.set('endDate', endDate);
    return this.http.get<HiringGapResponse>(`${this.apiUrl}/hiring-gap`, { params });
  }
}
