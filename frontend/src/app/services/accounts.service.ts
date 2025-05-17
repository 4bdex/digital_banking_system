import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class AccountsService {
  constructor(private http:HttpClient) { }
  
  public getAccount(accountId : string, page : number, size : number):Observable<any> {
    return this.http.get(`${environment.backendHost}/accounts/${accountId}/pageOperations?page=${page}&size=${size}`);
  }

  public debit(accountId : string, amount : number, description:string){
    let data={accountId, amount, description}
    return this.http.post(`${environment.backendHost}/accounts/debit`,data);
  }

  public credit(accountId : string, amount : number, description:string){
    let data={accountId, amount, description}
    return this.http.post(`${environment.backendHost}/accounts/credit`,data);
  }

  public transfer(accountSource: string,accountDestination: string, amount : number, description:string){
    let data={accountSource, accountDestination, amount, description}
    return this.http.post(`${environment.backendHost}/accounts/transfer`,data);
  }
}
