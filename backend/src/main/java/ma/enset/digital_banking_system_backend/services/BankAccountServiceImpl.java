package ma.enset.digital_banking_system_backend.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.enset.digital_banking_system_backend.entities.*;
import ma.enset.digital_banking_system_backend.enums.*;
import ma.enset.digital_banking_system_backend.exceptions.BalanceNotSufficientException;
import ma.enset.digital_banking_system_backend.exceptions.BankAccountNotFoundException;
import ma.enset.digital_banking_system_backend.exceptions.CustomerNotFoundException;
import ma.enset.digital_banking_system_backend.repositories.*;
import ma.enset.digital_banking_system_backend.dtos.*;
import ma.enset.digital_banking_system_backend.mappers.BankAccountMapperImpl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class BankAccountServiceImpl implements BankAccountService {
    private CustomerRepository customerRepository;
    private BankAccountRepository bankAccountRepository;
    private AccountOperationRepository accountOperationRepository;
    private BankAccountMapperImpl dtoMapper;

    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        log.info("Saving new Customer");
        Customer customer = dtoMapper.fromCustomerDTO(customerDTO);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            customer.setCreatedBy(auth.getName());
        }
        Customer savedCustomer = customerRepository.save(customer);
        return dtoMapper.fromCustomer(savedCustomer);
    }

    @Override
    public CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId)
            throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null)
            throw new CustomerNotFoundException("Customer not found");
        CurrentAccount currentAccount = new CurrentAccount();
        currentAccount.setId(UUID.randomUUID().toString());
        // Set createdAt to a random date within the last 12 months
        currentAccount.setCreatedAt(randomPastDateWithinMonths(12));
        currentAccount.setBalance(initialBalance);
        currentAccount.setOverDraft(overDraft);
        currentAccount.setCustomer(customer);
        currentAccount.setStatus(AccountStatus.CREATED);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            currentAccount.setCreatedBy(auth.getName());
        }
        CurrentAccount savedBankAccount = bankAccountRepository.save(currentAccount);
        return dtoMapper.fromCurrentBankAccount(savedBankAccount);
    }

    @Override
    public SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId)
            throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null)
            throw new CustomerNotFoundException("Customer not found");
        SavingAccount savingAccount = new SavingAccount();
        savingAccount.setId(UUID.randomUUID().toString());
        // Set createdAt to a random date within the last 12 months
        savingAccount.setCreatedAt(randomPastDateWithinMonths(12));
        savingAccount.setBalance(initialBalance);
        savingAccount.setInterestRate(interestRate);
        savingAccount.setCustomer(customer);
        savingAccount.setStatus(AccountStatus.CREATED);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            savingAccount.setCreatedBy(auth.getName());
        }
        SavingAccount savedBankAccount = bankAccountRepository.save(savingAccount);
        return dtoMapper.fromSavingBankAccount(savedBankAccount);
    }

    @Override
    public List<CustomerDTO> listCustomers() {
        List<Customer> customers = customerRepository.findAll();
        List<CustomerDTO> customerDTOS = customers.stream()
                .map(customer -> dtoMapper.fromCustomer(customer))
                .collect(Collectors.toList());
        /*
         * List<CustomerDTO> customerDTOS=new ArrayList<>();
         * for (Customer customer:customers){
         * CustomerDTO customerDTO=dtoMapper.fromCustomer(customer);
         * customerDTOS.add(customerDTO);
         * }
         *
         */
        return customerDTOS;
    }

    @Override
    public BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("BankAccount not found"));
        if (bankAccount instanceof SavingAccount) {
            SavingAccount savingAccount = (SavingAccount) bankAccount;
            return dtoMapper.fromSavingBankAccount(savingAccount);
        } else {
            CurrentAccount currentAccount = (CurrentAccount) bankAccount;
            return dtoMapper.fromCurrentBankAccount(currentAccount);
        }
    }

    @Override
    public void debit(String accountId, double amount, String description)
            throws BankAccountNotFoundException, BalanceNotSufficientException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("BankAccount not found"));
        if (bankAccount.getBalance() < amount)
            throw new BalanceNotSufficientException("Balance not sufficient");
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(OperationType.DEBIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            accountOperation.setCreatedBy(auth.getName());
        }
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance() - amount);
        bankAccountRepository.save(bankAccount);
    }

    public void debit(String accountId, double amount, String description, Date operationDate)
            throws BankAccountNotFoundException, BalanceNotSufficientException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("BankAccount not found"));
        if (bankAccount.getBalance() < amount)
            throw new BalanceNotSufficientException("Balance not sufficient");
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(OperationType.DEBIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(operationDate != null ? operationDate : new Date());
        accountOperation.setBankAccount(bankAccount);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            accountOperation.setCreatedBy(auth.getName());
        }
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance() - amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void credit(String accountId, double amount, String description) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("BankAccount not found"));
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(OperationType.CREDIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            accountOperation.setCreatedBy(auth.getName());
        }
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance() + amount);
        bankAccountRepository.save(bankAccount);
    }

    public void credit(String accountId, double amount, String description, Date operationDate)
            throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("BankAccount not found"));
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(OperationType.CREDIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(operationDate != null ? operationDate : new Date());
        accountOperation.setBankAccount(bankAccount);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            accountOperation.setCreatedBy(auth.getName());
        }
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance() + amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void transfer(String accountIdSource, String accountIdDestination, double amount)
            throws BankAccountNotFoundException, BalanceNotSufficientException {
        debit(accountIdSource, amount, "Transfer to " + accountIdDestination);
        credit(accountIdDestination, amount, "Transfer from " + accountIdSource);
    }

    @Override
    public List<BankAccountDTO> bankAccountList() {
        List<BankAccount> bankAccounts = bankAccountRepository.findAll();
        List<BankAccountDTO> bankAccountDTOS = bankAccounts.stream().map(bankAccount -> {
            if (bankAccount instanceof SavingAccount) {
                SavingAccount savingAccount = (SavingAccount) bankAccount;
                return dtoMapper.fromSavingBankAccount(savingAccount);
            } else {
                CurrentAccount currentAccount = (CurrentAccount) bankAccount;
                return dtoMapper.fromCurrentBankAccount(currentAccount);
            }
        }).collect(Collectors.toList());
        return bankAccountDTOS;
    }

    @Override
    public CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer Not found"));
        return dtoMapper.fromCustomer(customer);
    }

    @Override
    public CustomerDTO updateCustomer(CustomerDTO customerDTO) {
        log.info("Saving new Customer");
        Customer customer = dtoMapper.fromCustomerDTO(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return dtoMapper.fromCustomer(savedCustomer);
    }

    @Override
    public void deleteCustomer(Long customerId) {
        List<BankAccount> accounts = bankAccountRepository.findByCustomerId(customerId);
        for (BankAccount account : accounts) {
            List<AccountOperation> operations = accountOperationRepository.findByBankAccountId(account.getId());
            accountOperationRepository.deleteAll(operations);
            bankAccountRepository.delete(account);
        }
        customerRepository.deleteById(customerId);
    }

    @Override
    public List<AccountOperationDTO> accountHistory(String accountId) {
        List<AccountOperation> accountOperations = accountOperationRepository.findByBankAccountId(accountId);
        return accountOperations.stream().map(op -> dtoMapper.fromAccountOperation(op)).collect(Collectors.toList());
    }

    @Override
    public AccountHistoryDTO getAccountHistory(String accountId, int page, int size)
            throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElse(null);
        if (bankAccount == null)
            throw new BankAccountNotFoundException("Account not Found");
        Page<AccountOperation> accountOperations = accountOperationRepository
                .findByBankAccountIdOrderByOperationDateDesc(accountId, PageRequest.of(page, size));
        AccountHistoryDTO accountHistoryDTO = new AccountHistoryDTO();
        List<AccountOperationDTO> accountOperationDTOS = accountOperations.getContent().stream()
                .map(op -> dtoMapper.fromAccountOperation(op)).collect(Collectors.toList());
        accountHistoryDTO.setAccountOperationDTOS(accountOperationDTOS);
        accountHistoryDTO.setAccountId(bankAccount.getId());
        accountHistoryDTO.setBalance(bankAccount.getBalance());
        accountHistoryDTO.setCurrentPage(page);
        accountHistoryDTO.setPageSize(size);
        accountHistoryDTO.setTotalPages(accountOperations.getTotalPages());
        return accountHistoryDTO;
    }

    @Override
    public List<CustomerDTO> searchCustomers(String keyword) {
        List<Customer> customers = customerRepository.searchCustomer(keyword);
        List<CustomerDTO> customerDTOS = customers.stream().map(cust -> dtoMapper.fromCustomer(cust))
                .collect(Collectors.toList());
        return customerDTOS;
    }

    @Override
    public List<BankAccountDTO> getCustomerAccounts(Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null)
            throw new CustomerNotFoundException("Customer not found");
        List<BankAccount> bankAccounts = bankAccountRepository.findByCustomerId(customerId);
        List<BankAccountDTO> bankAccountDTOS = bankAccounts.stream().map(bankAccount -> {
            if (bankAccount instanceof SavingAccount) {
                SavingAccount savingAccount = (SavingAccount) bankAccount;
                return dtoMapper.fromSavingBankAccount(savingAccount);
            } else {
                CurrentAccount currentAccount = (CurrentAccount) bankAccount;
                return dtoMapper.fromCurrentBankAccount(currentAccount);
            }
        }).collect(Collectors.toList());
        return bankAccountDTOS;
    }

    @Override
    public void deleteAccount(String accountId) throws BankAccountNotFoundException {
        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("Account not found"));
        List<AccountOperation> operations = accountOperationRepository.findByBankAccountId(accountId);
        accountOperationRepository.deleteAll(operations);
        bankAccountRepository.delete(account);
    }

    @Override
    public DashboardStatsDTO getDashboardStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();
        stats.setTotalCustomers(customerRepository.count());
        stats.setTotalAccounts(bankAccountRepository.count());
        stats.setTotalBalance(bankAccountRepository.findAll().stream().mapToDouble(BankAccount::getBalance).sum());

        // Account type counts
        List<BankAccount> allAccounts = bankAccountRepository.findAll();
        Map<String, Long> typeCounts = allAccounts.stream()
                .collect(Collectors.groupingBy(a -> a instanceof SavingAccount ? "SAVING" : "CURRENT",
                        Collectors.counting()));
        stats.setAccountTypeCounts(typeCounts);

        // Monthly new accounts (last 6 months)
        Map<String, Long> monthlyNewAccounts = new HashMap<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM");
        allAccounts.forEach(acc -> {
            LocalDate date = acc.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            String key = date.format(fmt);
            monthlyNewAccounts.put(key, monthlyNewAccounts.getOrDefault(key, 0L) + 1);
        });
        stats.setMonthlyNewAccounts(monthlyNewAccounts);

        // Monthly transactions (sum of all operations per month, last 6 months)
        List<AccountOperation> allOps = accountOperationRepository.findAll();
        Map<String, Double> monthlyTransactions = new HashMap<>();
        allOps.forEach(op -> {
            LocalDate date = op.getOperationDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            String key = date.format(fmt);
            monthlyTransactions.put(key, monthlyTransactions.getOrDefault(key, 0.0) + op.getAmount());
        });
        stats.setMonthlyTransactions(monthlyTransactions);

        return stats;
    }

    // Utility method to generate a random date within the last N months
    private Date randomPastDateWithinMonths(int months) {
        java.util.Random rand = new java.util.Random();
        LocalDate now = LocalDate.now();
        // Pick a random month offset (0 = this month, up to months-1)
        int randomMonthOffset = rand.nextInt(months);
        LocalDate baseMonth = now.minusMonths(randomMonthOffset);
        int maxDay = baseMonth.lengthOfMonth();
        int randomDay = rand.nextInt(maxDay) + 1; // 1 to maxDay
        LocalDate randomDate = baseMonth.withDayOfMonth(randomDay);
        return java.util.Date.from(randomDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

}
