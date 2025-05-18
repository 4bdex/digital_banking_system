import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from "@angular/router";
import { Customer } from "../model/customer.model";
import { NgIf, NgFor, AsyncPipe, CommonModule } from '@angular/common';
import { Observable } from 'rxjs';
import { Account, AccountDetails } from '../model/account.model';
import { AccountsService } from '../services/accounts.service';
import { ReactiveFormsModule } from '@angular/forms';
import { CustomerService } from '../services/customer.service';

@Component({
  selector: 'app-customer-accounts',
  templateUrl: './customer-accounts.component.html',
  styleUrls: ['./customer-accounts.component.css'],
  imports: [CommonModule, ReactiveFormsModule,NgIf, NgFor, AsyncPipe, RouterModule],
  standalone: true
})
export class CustomerAccountsComponent implements OnInit {
  customerId!: string;
  customer!: Customer;
  accounts!: Observable<Array<Account>>;
  errorMessage: any;
  constructor(private route: ActivatedRoute, private CustomerService: CustomerService, private router: Router) {
    this.customer = this.router.getCurrentNavigation()?.extras.state as Customer;
  }

  ngOnInit(): void {
    this.customerId = this.route.snapshot.params['id'];
    this.loadAccounts();

  }

  loadAccounts() {
    this.accounts = this.CustomerService.getAccountsByCustomer(this.customerId);


  }

}
