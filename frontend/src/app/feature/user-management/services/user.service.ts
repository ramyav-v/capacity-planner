import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { AppUser, AppUserDto } from '../user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly apiUrl = `${environment.CAPACITY_PLANNER_API}/users`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<AppUser[]> {
    return this.http.get<AppUser[]>(this.apiUrl);
  }

  getById(id: number): Observable<AppUser> {
    return this.http.get<AppUser>(`${this.apiUrl}/${id}`);
  }

  create(dto: AppUserDto): Observable<AppUser> {
    return this.http.post<AppUser>(this.apiUrl, dto);
  }

  update(id: number, dto: AppUserDto): Observable<AppUser> {
    return this.http.put<AppUser>(`${this.apiUrl}/${id}`, dto);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
