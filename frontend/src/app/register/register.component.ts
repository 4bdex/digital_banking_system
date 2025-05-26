import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../services/auth.service';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent implements OnInit {
  registerForm!: FormGroup;
  message: string = '';
  error: string = '';

  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router) { }

  ngOnInit(): void {
    this.registerForm = this.fb.group({
      username: this.fb.control('', [Validators.required, Validators.minLength(3)]),
      password: this.fb.control('', [Validators.required, Validators.minLength(6)]),
      role: this.fb.control('USER')
    });
  }

  handleRegister() {
    const { username, password, role } = this.registerForm.value;
    this.authService.register(username, password, role).subscribe({
      next: (res: any) => {
        this.message = res.message || 'Registration successful.';
        this.error = '';
        this.registerForm.reset();

      },
      error: (err) => {
        this.error = err.error?.message || err.error || 'Registration failed.';
        this.message = '';
      }
    });
  }

  goToLogin() {
    this.router.navigate(['/login']);
  }
}
