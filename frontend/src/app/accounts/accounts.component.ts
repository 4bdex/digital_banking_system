import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from "@angular/forms";
import { AccountsService } from "../services/accounts.service";
import { catchError, Observable, throwError } from "rxjs";
import { AccountDetails } from "../model/account.model";
import { NgFor } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-accounts',
  templateUrl: './accounts.component.html',
  styleUrls: ['./accounts.component.css'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, RouterModule],
})
export class AccountsComponent implements OnInit {
  accountFormGroup!: FormGroup;
  currentPage: number = 0;
  pageSize: number = 5;
  accountObservable!: Observable<AccountDetails>
  operationFromGroup!: FormGroup;
  errorMessage!: string;
  accounts: any[] = [];
  allAccounts: any[] = [];
  searchTerm: string = '';

  constructor(
    private fb: FormBuilder,
    private accountService: AccountsService,
    private route: ActivatedRoute,
    private router: Router
  ) {

  }

  ngOnInit(): void {
    this.accountFormGroup = this.fb.group({
      accountId: this.fb.control('')
    });

    this.route.params.subscribe(params => {
      const accountId = params['id'];
      if (accountId) {

        this.accountFormGroup.get('accountId')?.setValue(accountId);

        this.handleSearchAccount();
      }
    });

    this.operationFromGroup = this.fb.group({
      operationType: this.fb.control(null),
      amount: this.fb.control(0),
      description: this.fb.control(null),
      accountDestination: this.fb.control(null)
    });

    this.loadAccounts();
  }


  handleSearchAccount() {
    let accountId: string = this.accountFormGroup.value.accountId;
    this.accountObservable = this.accountService.getAccount(accountId, this.currentPage, this.pageSize).pipe(
      catchError(err => {
        this.errorMessage = err.message;
        return throwError(err);
      })
    );
  }

  gotoPage(page: number) {
    this.currentPage = page;
    this.handleSearchAccount();
  }


  loadAccounts() {
    this.accountService.getAllAccounts().subscribe((accounts) => {
      this.accounts = accounts;
      this.allAccounts = accounts;
    });
  }

  searchAccounts() {
    const term = this.searchTerm.trim().toLowerCase();
    if (!term) {
     
      this.accounts = this.allAccounts;
      return;
    }
    this.accounts = this.allAccounts.filter(acc =>
      acc.id.toLowerCase().includes(term) ||
      (acc.customer?.name?.toLowerCase().includes(term) || '')
    );
  }
  deleteAccount(account: any) {
    if (confirm('Are you sure you want to delete this account?')) {
      this.accountService.deleteAccount(account.id).subscribe({
        next: () => {
          this.accounts = this.accounts.filter(acc => acc.id !== account.id);
        },
        error: (err) => {
          console.error('Error deleting account:', err);
          alert('Failed to delete account.');
        }
      });
    }
  }

  editAccount(account: any) {
   
    alert('Edit account feature to be implemented.');
  }
}
