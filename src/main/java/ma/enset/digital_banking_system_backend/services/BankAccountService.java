package ma.enset.digital_banking_system_backend.services;


import java.util.List;

import ma.enset.digital_banking_system_backend.entities.BankAccount;
import ma.enset.digital_banking_system_backend.entities.Customer;
public interface BankAccountService {
   
    Customer saveCustomer(Customer customer);
    BankAccount saveCurrentAccount(double initialBalance, double overDraft, Long customerId);
    BankAccount saveSavingAccount(double initialBalance, double interestRate, Long customerId);
    List<Customer> listCustomers();
    List<BankAccount> bankAccountList();
    BankAccount getBankAccount(String accountId);
    void debit(String accountId, double amount, String description);
    void credit(String accountId, double amount, String description);
    void transfer(String accountIdSource, String accountIdDestination, double amount);


}
