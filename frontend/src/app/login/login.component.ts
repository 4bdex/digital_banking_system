import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {

  formLogin!: FormGroup;
  constructor(private fb: FormBuilder,private authService : AuthService, private router: Router) {

  }

  ngOnInit(): void {
    this.formLogin = this.fb.group({
      username: this.fb.control(null, [Validators.required, Validators.minLength(3)]),
      password: this.fb.control(null, [Validators.required, Validators.minLength(3)])
    });
  }

  handleLogin() {
    let username = this.formLogin.value.username;
    let password = this.formLogin.value.password;
    this.authService.login(username, password).subscribe({
      next: (data) => {
        this.authService.loadProfile(data);
        if (this.authService.roles?.includes('ROLE_ADMIN')) {
        this.router.navigateByUrl('/accounts');
      } else {
        this.router.navigateByUrl('/customers');
      }

      },
      error: (err) => {
        console.log(err);
      }
    });
  }
}



