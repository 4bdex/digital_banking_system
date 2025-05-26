package ma.enset.digital_banking_system_backend;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import ma.enset.digital_banking_system_backend.dtos.BankAccountDTO;
import ma.enset.digital_banking_system_backend.dtos.CurrentBankAccountDTO;
import ma.enset.digital_banking_system_backend.dtos.CustomerDTO;
import ma.enset.digital_banking_system_backend.dtos.SavingBankAccountDTO;
import ma.enset.digital_banking_system_backend.entities.AccountOperation;
import ma.enset.digital_banking_system_backend.entities.CurrentAccount;
import ma.enset.digital_banking_system_backend.entities.Customer;
import ma.enset.digital_banking_system_backend.entities.SavingAccount;
import ma.enset.digital_banking_system_backend.enums.AccountStatus;
import ma.enset.digital_banking_system_backend.enums.OperationType;
import ma.enset.digital_banking_system_backend.exceptions.CustomerNotFoundException;
import ma.enset.digital_banking_system_backend.repositories.*;
import ma.enset.digital_banking_system_backend.services.BankAccountService;

@SpringBootApplication
public class DigitalBankingSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(DigitalBankingSystemApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(BankAccountService bankAccountService) {
        return args -> {
            // Generate 20 random customers
            for (int i = 0; i < 20; i++) {
                CustomerDTO customer = new CustomerDTO();
                customer.setName("Customer" + i);
                customer.setEmail("customer" + i + "@mail.com");
                bankAccountService.saveCustomer(customer);
            }
            // For each customer, create 1-2 current and 1-2 saving accounts with random
            // creation dates in the last 12 months
            bankAccountService.listCustomers().forEach(customer -> {
                try {
                    int numCurrent = 1 + (int) (Math.random() * 2);
                    int numSaving = 1 + (int) (Math.random() * 2);
                    for (int j = 0; j < numCurrent; j++) {
                        CurrentBankAccountDTO acc = bankAccountService.saveCurrentBankAccount(Math.random() * 90000,
                                9000, customer.getId());
                        // Set a random creation date in the last 12 months (not current date)
                        Date randomDate = new Date(
                                System.currentTimeMillis() - (long) (Math.random() * 365 * 24 * 60 * 60 * 1000L));
                        acc.setCreatedAt(randomDate);
                        // Optionally, persist the random date if your service supports it
                    }
                    for (int j = 0; j < numSaving; j++) {
                        SavingBankAccountDTO acc = bankAccountService.saveSavingBankAccount(Math.random() * 120000, 5.5,
                                customer.getId());
                        Date randomDate = new Date(
                                System.currentTimeMillis() - (long) (Math.random() * 365 * 24 * 60 * 60 * 1000L));
                        acc.setCreatedAt(randomDate);
                        // Optionally, persist the random date if your service supports it
                    }
                } catch (CustomerNotFoundException e) {
                    e.printStackTrace();
                }
            });
            // For each account, create 10-20 random operations with random dates in the
            // last 12 months
            List<BankAccountDTO> bankAccounts = bankAccountService.bankAccountList();
            for (BankAccountDTO bankAccount : bankAccounts) {
                String accountId = bankAccount instanceof SavingBankAccountDTO
                        ? ((SavingBankAccountDTO) bankAccount).getId()
                        : ((CurrentBankAccountDTO) bankAccount).getId();
                int numOps = 10 + (int) (Math.random() * 11);
                for (int i = 0; i < numOps; i++) {
                    double amount = Math.random() * 12000;
                    boolean isCredit = Math.random() > 0.5;
                    Date randomOpDate = new Date(
                            System.currentTimeMillis() - (long) (Math.random() * 365 * 24 * 60 * 60 * 1000L));
                    if (isCredit) {
                        // Use new credit method with random date
                        ((ma.enset.digital_banking_system_backend.services.BankAccountServiceImpl) bankAccountService)
                                .credit(accountId, amount, "Credit", randomOpDate);
                    } else {
                        BankAccountDTO acc = bankAccountService.getBankAccount(accountId);
                        double balance = acc instanceof SavingBankAccountDTO ? ((SavingBankAccountDTO) acc).getBalance()
                                : ((CurrentBankAccountDTO) acc).getBalance();
                        if (balance >= amount) {
                            // Use new debit method with random date
                            ((ma.enset.digital_banking_system_backend.services.BankAccountServiceImpl) bankAccountService)
                                    .debit(accountId, amount, "Debit", randomOpDate);
                        }
                    }
                }
            }
        };
    }

    // @Bean
    CommandLineRunner start(CustomerRepository customerRepository,
            BankAccountRepository bankAccountRepository,
            AccountOperationRepository accountOperationRepository) {
        return args -> {
            Stream.of("M", "K", "O").forEach(name -> {
                Customer customer = new Customer();
                customer.setName(name);
                customer.setEmail(name + "@gmail.com");
                customerRepository.save(customer);
            });
            customerRepository.findAll().forEach(cust -> {
                CurrentAccount currentAccount = new CurrentAccount();
                currentAccount.setId(UUID.randomUUID().toString());
                currentAccount.setBalance(Math.random() * 90000);
                currentAccount.setCreatedAt(new Date());
                currentAccount.setStatus(AccountStatus.CREATED);
                currentAccount.setCustomer(cust);
                currentAccount.setOverDraft(9000);
                bankAccountRepository.save(currentAccount);

                SavingAccount savingAccount = new SavingAccount();
                savingAccount.setId(UUID.randomUUID().toString());
                savingAccount.setBalance(Math.random() * 90000);
                savingAccount.setCreatedAt(new Date());
                savingAccount.setStatus(AccountStatus.CREATED);
                savingAccount.setCustomer(cust);
                savingAccount.setInterestRate(5.5);
                bankAccountRepository.save(savingAccount);

            });
            bankAccountRepository.findAll().forEach(acc -> {
                for (int i = 0; i < 10; i++) {
                    AccountOperation accountOperation = new AccountOperation();
                    accountOperation.setOperationDate(new Date());
                    accountOperation.setAmount(Math.random() * 12000);
                    accountOperation.setType(Math.random() > 0.5 ? OperationType.DEBIT : OperationType.CREDIT);
                    accountOperation.setBankAccount(acc);
                    accountOperationRepository.save(accountOperation);
                }

            });
        };

    }

}
