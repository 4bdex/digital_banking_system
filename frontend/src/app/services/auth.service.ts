import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { jwtDecode } from 'jwt-decode';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  isAuthenticated: boolean = false;
  roles: any = "";
  username: any;
  jwtToken!: any;

  constructor(private http: HttpClient) { }
  public login(username: string, password: string) {
    let options = {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    };
    let params = new HttpParams()
      .set('username', username)
      .set('password', password);
    return this.http.post(`http://localhost:8084/auth/login`, params, options);
  }
  loadProfile(data: any) {
    this.jwtToken = data['access_token'];
    this.isAuthenticated = true;
    let jwtDecoded: any = jwtDecode(this.jwtToken);
    this.username = jwtDecoded.sub;
    this.roles = jwtDecoded.scope;
    console.log(jwtDecoded);
  }
  logout() {
    this.isAuthenticated = false;
    this.roles = null;
    this.username = null;
    this.jwtToken = null;
  }

}
