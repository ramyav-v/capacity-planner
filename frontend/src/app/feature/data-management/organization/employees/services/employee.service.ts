import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../../../environments/environment';
import { Employee, EmployeeDto } from '../employee.model';

@Injectable({
  providedIn: 'root'
})
export class EmployeeService {
  private readonly apiUrl = `${environment.CAPACITY_PLANNER_API}/employees`;

  constructor(private http: HttpClient) {}

  getAll(role?: string, company?: string, isActive?: boolean): Observable<Employee[]> {
    let params = new HttpParams();
    if (role) params = params.set('role', role);
    if (company) params = params.set('company', company);
    if (isActive !== undefined) params = params.set('isActive', String(isActive));
    return this.http.get<Employee[]>(this.apiUrl, { params });
  }

  getById(id: number): Observable<Employee> {
    return this.http.get<Employee>(`${this.apiUrl}/${id}`);
  }

  create(dto: EmployeeDto): Observable<Employee> {
    return this.http.post<Employee>(this.apiUrl, dto);
  }

  update(id: number, dto: EmployeeDto): Observable<Employee> {
    return this.http.put<Employee>(`${this.apiUrl}/${id}`, dto);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
