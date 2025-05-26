import { Routes } from '@angular/router';
import { CustomersComponent } from './customers/customers.component';
import { AccountsComponent } from './accounts/accounts.component';
import { NewCustomerComponent } from './new-customer/new-customer.component';
import { CustomerAccountsComponent } from './customer-accounts/customer-accounts.component';
import { EditCustomerComponent } from './edit-customer/edit-customer.component';
import { AccountOperationsComponent } from './account-operations/account-operations.component';
import { NewAccountComponent } from './new-account/new-account.component';
import { LoginComponent } from './login/login.component';
import { authentificationGuard } from './guards/authentification.guard';
import { authorizationGuard } from './guards/authorization.guard';
import { NotAuthorizedComponent } from './not-authorized/not-authorized.component';
import { ChangePasswordComponent } from './change-password/change-password.component';
import { RegisterComponent } from './register/register.component';
import { DashboardComponent } from './dashboard/dashboard.component';

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'change-password', component: ChangePasswordComponent },
  { path: 'dashboard', component: DashboardComponent },
  {
    path: '',
    canActivateChild: [authentificationGuard],
    children: [
      { path: 'customers', component: CustomersComponent },
      { path: 'account-operations', component: AccountOperationsComponent },
      { path: 'account-operations/:id', component: AccountOperationsComponent },
      { path: 'accounts', component: AccountsComponent, canActivate: [authorizationGuard], data: { roles: ['ROLE_ADMIN'] } },
      { path: 'new-customer', component: NewCustomerComponent, canActivate: [authorizationGuard], data: { roles: ['ROLE_ADMIN'] } },
      { path: 'customer-accounts/:id', component: CustomerAccountsComponent },
      { path: 'edit-customer/:id', component: EditCustomerComponent, canActivate: [authorizationGuard], data: { roles: ['ROLE_ADMIN'] } },
      { path: 'new-account', component: NewAccountComponent, canActivate: [authorizationGuard], data: { roles: ['ROLE_ADMIN'] } },
      { path: 'not-authorized', component: NotAuthorizedComponent },
    ]
  },
];

