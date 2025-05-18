package ma.enset.digital_banking_system_backend.services;


import java.util.List;

import ma.enset.digital_banking_system_backend.dtos.AccountHistoryDTO;
import ma.enset.digital_banking_system_backend.dtos.AccountOperationDTO;
import ma.enset.digital_banking_system_backend.dtos.BankAccountDTO;
import ma.enset.digital_banking_system_backend.dtos.CurrentBankAccountDTO;
import ma.enset.digital_banking_system_backend.dtos.CustomerDTO;
import ma.enset.digital_banking_system_backend.dtos.SavingBankAccountDTO;
import ma.enset.digital_banking_system_backend.exceptions.BalanceNotSufficientException;
import ma.enset.digital_banking_system_backend.exceptions.BankAccountNotFoundException;
import ma.enset.digital_banking_system_backend.exceptions.CustomerNotFoundException;
public interface BankAccountService {
    CustomerDTO saveCustomer(CustomerDTO customerDTO);
    CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException;
    SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException;
    List<CustomerDTO> listCustomers();
    BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException;
    void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException;
    void credit(String accountId, double amount, String description) throws BankAccountNotFoundException;
    void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException, BalanceNotSufficientException;

    List<BankAccountDTO> bankAccountList();

    CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException;

    CustomerDTO updateCustomer(CustomerDTO customerDTO);

    void deleteCustomer(Long customerId);


    List<AccountOperationDTO> accountHistory(String accountId);

    AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException;

    List<CustomerDTO> searchCustomers(String keyword);

    List<BankAccountDTO> getCustomerAccounts(Long customerId) throws CustomerNotFoundException;

    void deleteAccount(String accountId) throws BankAccountNotFoundException;


}
