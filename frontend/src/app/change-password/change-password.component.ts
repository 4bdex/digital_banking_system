import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../services/auth.service';
import { NgIf, CommonModule } from '@angular/common';

@Component({
  selector: 'app-change-password',
  standalone: true,
  imports: [ReactiveFormsModule, NgIf, CommonModule],
  templateUrl: './change-password.component.html',
  styleUrl: './change-password.component.css'
})
export class ChangePasswordComponent {
  changePasswordForm!: FormGroup;
  message: string = '';
  error: string = '';

  constructor(private fb: FormBuilder, private authService: AuthService) { }

  ngOnInit(): void {
    this.changePasswordForm = this.fb.group({
      currentPassword: this.fb.control('', [Validators.required]),
      newPassword: this.fb.control('', [Validators.required, Validators.minLength(6)])
    });
  }

  handleChangePassword() {
    const { currentPassword, newPassword } = this.changePasswordForm.value;
    this.authService.changePassword(currentPassword, newPassword).subscribe({
      next: (res: any) => {
        this.message = res.message || 'Password changed successfully.';
        this.error = '';
        this.changePasswordForm.reset();
      },
      error: (err) => {
        this.error = err.error || 'Failed to change password.';
        this.message = '';
      }
    });
  }
}
