import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../../../environments/environment';
import { Project, ProjectDto } from '../project.model';

@Injectable({
  providedIn: 'root'
})
export class ProjectService {
  private readonly apiUrl = `${environment.CAPACITY_PLANNER_API}/projects`;

  constructor(private http: HttpClient) {}

  getAll(region?: string, status?: string, isActive?: boolean): Observable<Project[]> {
    let params = new HttpParams();
    if (region) params = params.set('region', region);
    if (status) params = params.set('status', status);
    if (isActive !== undefined) params = params.set('isActive', String(isActive));
    return this.http.get<Project[]>(this.apiUrl, { params });
  }

  getById(id: number): Observable<Project> {
    return this.http.get<Project>(`${this.apiUrl}/${id}`);
  }

  create(dto: ProjectDto): Observable<Project> {
    return this.http.post<Project>(this.apiUrl, dto);
  }

  update(id: number, dto: ProjectDto): Observable<Project> {
    return this.http.put<Project>(`${this.apiUrl}/${id}`, dto);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
