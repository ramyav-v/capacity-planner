import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../../environments/environment';
import { AllocationGroup, AllocationDto, AllocationWeek, QuarterAllocationRequest } from '../allocation.model';

@Injectable({
  providedIn: 'root'
})
export class AllocationService {
  private readonly apiUrl = `${environment.CAPACITY_PLANNER_API}/allocation`;

  constructor(private http: HttpClient) {}

  getAll(employeeId?: number, projectId?: number): Observable<AllocationGroup[]> {
    let params = new HttpParams();
    if (employeeId != null) params = params.set('employeeId', String(employeeId));
    if (projectId != null) params = params.set('projectId', String(projectId));
    return this.http.get<AllocationGroup[]>(this.apiUrl, { params });
  }

  create(dto: AllocationDto): Observable<AllocationWeek> {
    return this.http.post<AllocationWeek>(this.apiUrl, dto);
  }

  createQuarter(request: QuarterAllocationRequest): Observable<AllocationWeek[]> {
    return this.http.post<AllocationWeek[]>(`${this.apiUrl}/quarter`, request);
  }

  update(dto: AllocationDto): Observable<AllocationWeek> {
    return this.http.put<AllocationWeek>(this.apiUrl, dto);
  }

  updateQuarter(request: QuarterAllocationRequest): Observable<AllocationWeek[]> {
    return this.http.put<AllocationWeek[]>(this.apiUrl, request);
  }

  delete(employeeId: number, projectId: number): Observable<void> {
    const params = new HttpParams()
      .set('employeeId', String(employeeId))
      .set('projectId', String(projectId));
    return this.http.delete<void>(this.apiUrl, { params });
  }
}
