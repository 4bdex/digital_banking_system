import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { AccountsService } from '../services/accounts.service';
import { CustomerService } from '../services/customer.service';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { Customer } from '../model/customer.model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-new-account',
  templateUrl: './new-account.component.html',
  styleUrls: ['./new-account.component.css'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule]
})
export class NewAccountComponent implements OnInit {
  addAccountFormGroup!: FormGroup;
  customers$!: Observable<Customer[]>;

  constructor(
    private fb: FormBuilder,
    private accountsService: AccountsService,
    private customerService: CustomerService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.addAccountFormGroup = this.fb.group({
      type: ['CURRENT', Validators.required],
      balance: [0, [Validators.required, Validators.min(0)]],
      customerId: ['', Validators.required],
      overDraft: [0],
      interestRate: [0], 
    });
    this.customers$ = this.customerService.getCustomers();
  }

  handleAddAccount() {
    if (this.addAccountFormGroup.invalid) {
      console.log('Form invalid', this.addAccountFormGroup.value);
      return;
    }
    const { type, balance, customerId, overDraft, interestRate } = this.addAccountFormGroup.value;
    if (type === 'CURRENT') {
      this.accountsService.addCurrentAccount({ balance, overDraft, customerId }).subscribe({
        next: () => {
          this.router.navigate(['/accounts']);
        },
        error: (err) => {
          alert('Error creating account: ' + err.message);
        }
      });
    } else if (type === 'SAVINGS') {
      this.accountsService.addSavingAccount({ balance, interestRate, customerId }).subscribe({
        next: () => {
          this.router.navigate(['/accounts']);
        },
        error: (err) => {
          alert('Error creating account: ' + err.message);
        }
      });
    }
  }
}
