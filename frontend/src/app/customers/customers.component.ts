import { Component, OnInit } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { CustomerService } from "../services/customer.service";
import { catchError, map, Observable, throwError } from "rxjs";
import { Customer } from "../model/customer.model";
import { FormBuilder, FormGroup, ReactiveFormsModule } from "@angular/forms";
import { Router } from "@angular/router";
import { NgIf, NgFor, AsyncPipe, CommonModule } from '@angular/common';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-customers',
  templateUrl: './customers.component.html',
  styleUrls: ['./customers.component.css'],
  imports: [CommonModule, ReactiveFormsModule, NgIf, NgFor, AsyncPipe],
  standalone: true
})
export class CustomersComponent implements OnInit {
  handleAddCustomer() {
    throw new Error('Method not implemented.');
  }
  customers!: Observable<Array<Customer>>;
  errorMessage!: string;
  searchFormGroup: FormGroup | undefined;
  constructor(private customerService: CustomerService, private fb: FormBuilder, private router: Router, public authService: AuthService) { }

  ngOnInit(): void {
    this.searchFormGroup = this.fb.group({
      keyword: this.fb.control("")
    });
    this.handleSearchCustomers();
  }
  handleSearchCustomers() {
    let kw = this.searchFormGroup?.value.keyword;
    this.customers = this.customerService.searchCustomers(kw).pipe(
      catchError(err => {
        this.errorMessage = err.message;
        return throwError(err);
      })
    );
  }

  handleDeleteCustomer(c: Customer) {
    let conf = confirm("Are you sure?");
    if (!conf) return;
    this.customerService.deleteCustomer(c.id).subscribe({
      next: (resp) => {
        this.customers = this.customers.pipe(
          map(data => {
            let index = data.indexOf(c);
            data.slice(index, 1)
            return data;
          })
        );
      },
      error: err => {
        console.log(err);
      }
    })
  }
  handleEditCustomer(customer: Customer) {
    this.router.navigateByUrl("/edit-customer/" + customer.id, { state: customer });
  }

  handleCustomerAccounts(customer: Customer) {
    this.router.navigateByUrl("/customer-accounts/" + customer.id, { state: customer });
  }


}
