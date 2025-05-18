import { Routes } from '@angular/router';
import { CustomersComponent } from './customers/customers.component';
import { AccountsComponent } from './accounts/accounts.component';
import { NewCustomerComponent } from './new-customer/new-customer.component';
import { CustomerAccountsComponent } from './customer-accounts/customer-accounts.component';
import { EditCustomerComponent } from './edit-customer/edit-customer.component';
import { AccountOperationsComponent } from './account-operations/account-operations.component';
import { NewAccountComponent } from './new-account/new-account.component';

export const routes: Routes = [
  { path: "", redirectTo: "/customers", pathMatch: "full" },
  { path: "customers", component: CustomersComponent },
  { path: "account-operations", component: AccountOperationsComponent },
  { path: 'account-operations/:id', component: AccountOperationsComponent },
  { path: 'accounts', component: AccountsComponent },
  { path: "new-customer", component: NewCustomerComponent },
  { path: "customer-accounts/:id", component: CustomerAccountsComponent },
  { path: 'edit-customer/:id', component: EditCustomerComponent },
  { path: 'new-account', component: NewAccountComponent },

];

