import { Injectable, OnInit } from '@angular/core';
import { User } from '../../dto/user';
import { AuthService } from './auth.service';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserRequestService {
  private apiUrl = 'http://localhost:8080/api/user-requests';
  user!: User;
  constructor(
    private authService: AuthService,
    private http: HttpClient
  ) { }

  getAllPost(type: string): Observable<any>{
    const token = this.authService.getToken();  // Lấy JWT từ AuthService
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.post<any>(`${this.apiUrl}/posts?type=${type}`, 
       {headers});
  }


}