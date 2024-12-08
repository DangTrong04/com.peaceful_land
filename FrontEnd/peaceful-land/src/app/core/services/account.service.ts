import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from './auth.service';
import { User } from '../../dto/user';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AccountService {
  private apiUrl = 'http://localhost:8080/api/account';
  constructor(
    private http:HttpClient,
    private authService:AuthService,
  ) { }

  purchaseRole(user_id: number, role: number, day: number ): Observable<User>{
    const token = this.authService.getToken();  // Lấy JWT từ AuthService
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    return this.http.post<User>(`${this.apiUrl}/purchase-role`,
      {user_id,role,day},
      { headers });
  }
}
