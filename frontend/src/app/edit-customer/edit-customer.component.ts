import { Component, NgModule, OnInit } from '@angular/core';
import { Customer } from '../model/customer.model';
import {ActivatedRoute, Router} from "@angular/router";
import { CommonModule } from '@angular/common';

import { FormBuilder, FormsModule } from '@angular/forms';
import { CustomerService } from '../services/customer.service';

@Component({
  selector: 'app-edit-customer',
  imports: [CommonModule, CommonModule, FormsModule],
  templateUrl: './edit-customer.component.html',
  styleUrl: './edit-customer.component.css'
})


export class EditCustomerComponent implements OnInit {
  customerId! : string ;
  customer! : Customer;

  constructor(private fb : FormBuilder,private route: ActivatedRoute , private customerService:CustomerService, private router:Router) { 
    this.customer=this.router.getCurrentNavigation()?.extras.state as Customer;
  }
  
editCustomer() {
  let customer:Customer=this.customer;
  this.customerService.editCustomer(customer).subscribe({
    next : data=>{
      alert("Customer has been successfully edited!");
   
      this.router.navigateByUrl("/customers");
    },
    error : err => {
      console.log(err);
    }
  });
}



  ngOnInit(): void {
    this.customerId = this.route.snapshot.params['id'];

  }

}
