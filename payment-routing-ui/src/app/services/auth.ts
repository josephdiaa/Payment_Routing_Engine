import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

interface LoginRequest {
  username: string;
  password: string;
}

interface LoginResponse {
  token: string;
}

interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly baseUrl = 'http://localhost:8090/api/auth';

  constructor(private http: HttpClient) {}

  login(username: string, password: string): Observable<ApiResponse<LoginResponse>> {
    const request: LoginRequest = {
      username: username,
      password: password
    };

    return this.http.post<ApiResponse<LoginResponse>>(
      `${this.baseUrl}/login`,
      request
    );
  }

  saveToken(token: string): void {
    localStorage.setItem('jwt_token', token);
  }

  getToken(): string | null {
    return localStorage.getItem('jwt_token');
  }

  isLoggedIn(): boolean {
    return this.getToken() !== null;
  }

  logout(): void {
    localStorage.removeItem('jwt_token');
  }
}