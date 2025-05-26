import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface DashboardStats {
    totalCustomers: number;
    totalAccounts: number;
    totalBalance: number;
    accountTypeCounts: { [type: string]: number };
    monthlyNewAccounts: { [month: string]: number };
    monthlyTransactions: { [month: string]: number };
}

@Injectable({ providedIn: 'root' })
export class DashboardService {
    constructor(private http: HttpClient) { }

    getStats(): Observable<DashboardStats> {
        return this.http.get<DashboardStats>(`${environment.backendHost}/dashboard/stats`);
    }
}
