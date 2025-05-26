import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
  imports: [CommonModule, RouterModule],
  standalone: true
})
export class NavbarComponent implements OnInit {
  constructor(public authService: AuthService, private router: Router) { }

  ngOnInit(): void {
  }

  isAdmin(): boolean {
    return this.authService.roles && this.authService.roles.includes('ROLE_ADMIN');
  }

  handleLogout() {
    this.authService.logout();
    this.router.navigateByUrl('/login');
  }


}
