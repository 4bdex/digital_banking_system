import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgChartsModule } from 'ng2-charts';
import { ChartConfiguration, ChartType } from 'chart.js';
import { DashboardService, DashboardStats } from '../services/dashboard.service';

@Component({
    selector: 'app-dashboard',
    standalone: true,
    imports: [CommonModule, NgChartsModule],
    templateUrl: './dashboard.component.html',
    styleUrl: './dashboard.component.css',
})
export class DashboardComponent implements OnInit {
    public barChartOptions: ChartConfiguration['options'] = { responsive: true };
    public barChartLabels: string[] = [];
    public barChartType: ChartType = 'line';
    public barChartLegend = true;
    public barChartData: any = [];

    public pieChartLabels: string[] = [];
    public pieChartData: any = { datasets: [{ data: [] }] };
    public pieChartType: ChartType = 'pie';

    public totalCustomers = 0;
    public totalAccounts = 0;
    public totalBalance = 0;
    public accountTypeCounts: { [type: string]: number } = {};

    public transactionsChartType: ChartType = 'line';
    public newAccountsChartType: ChartType = 'bar';
    public transactionsChartData: any = { labels: [], datasets: [] };
    public newAccountsChartData: any = { labels: [], datasets: [] };

    private dashboardService = inject(DashboardService);

    ngOnInit(): void {
        this.dashboardService.getStats().subscribe((stats: DashboardStats) => {
            this.totalCustomers = stats.totalCustomers;
            this.totalAccounts = stats.totalAccounts;
            this.totalBalance = stats.totalBalance;
            this.accountTypeCounts = stats.accountTypeCounts;

            // Line chart: monthly transactions
            const months = Object.keys(stats.monthlyTransactions).sort();
            this.transactionsChartData = {
                labels: months,
                datasets: [
                    { data: months.map(m => stats.monthlyTransactions[m] || 0), label: 'Transactions', fill: false, borderColor: '#007bff', tension: 0.3 }
                ]
            };

            // Bar chart: monthly new accounts
            this.newAccountsChartData = {
                labels: months,
                datasets: [
                    { data: months.map(m => stats.monthlyNewAccounts[m] || 0), label: 'New Accounts', backgroundColor: '#28a745' }
                ]
            };

            // Pie chart: account type distribution
            this.pieChartLabels = Object.keys(stats.accountTypeCounts);
            this.pieChartData = {
                labels: this.pieChartLabels,
                datasets: [{ data: this.pieChartLabels.map(l => stats.accountTypeCounts[l]) }]
            };
        });
    }
}
