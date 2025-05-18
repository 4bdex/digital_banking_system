package ma.enset.digital_banking_system_backend.web;

import ma.enset.digital_banking_system_backend.dtos.*;
import ma.enset.digital_banking_system_backend.exceptions.BalanceNotSufficientException;
import ma.enset.digital_banking_system_backend.exceptions.BankAccountNotFoundException;
import ma.enset.digital_banking_system_backend.exceptions.CustomerNotFoundException;
import ma.enset.digital_banking_system_backend.services.BankAccountService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
public class BankAccountRestAPI {
    private BankAccountService bankAccountService;

    public BankAccountRestAPI(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @GetMapping("/accounts/{accountId}")
    public BankAccountDTO getBankAccount(@PathVariable String accountId) throws BankAccountNotFoundException {
        return bankAccountService.getBankAccount(accountId);
    }

    @GetMapping("/accounts")
    public List<BankAccountDTO> listAccounts() {
        return bankAccountService.bankAccountList();
    }

    @GetMapping("/accounts/{accountId}/operations")
    public List<AccountOperationDTO> getHistory(@PathVariable String accountId) {
        return bankAccountService.accountHistory(accountId);
    }

    @GetMapping("/accounts/{accountId}/pageOperations")
    public AccountHistoryDTO getAccountHistory(
            @PathVariable String accountId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size) throws BankAccountNotFoundException {
        return bankAccountService.getAccountHistory(accountId, page, size);
    }

    @PostMapping("/accounts/debit")
    public DebitDTO debit(@RequestBody DebitDTO debitDTO)
            throws BankAccountNotFoundException, BalanceNotSufficientException {
        this.bankAccountService.debit(debitDTO.getAccountId(), debitDTO.getAmount(), debitDTO.getDescription());
        return debitDTO;
    }

    @PostMapping("/accounts/credit")
    public CreditDTO credit(@RequestBody CreditDTO creditDTO) throws BankAccountNotFoundException {
        this.bankAccountService.credit(creditDTO.getAccountId(), creditDTO.getAmount(), creditDTO.getDescription());
        return creditDTO;
    }

    @PostMapping("/accounts/transfer")
    public void transfer(@RequestBody TransferRequestDTO transferRequestDTO)
            throws BankAccountNotFoundException, BalanceNotSufficientException {
        this.bankAccountService.transfer(
                transferRequestDTO.getAccountSource(),
                transferRequestDTO.getAccountDestination(),
                transferRequestDTO.getAmount());
    }
    @DeleteMapping("/accounts/{accountId}")
    public void deleteAccount(@PathVariable String accountId) throws BankAccountNotFoundException {
        bankAccountService.deleteAccount(accountId);
    }


    @PostMapping("/accounts/current")
    public CurrentBankAccountDTO saveCurrentBankAccount(@RequestBody CurrentBankAccountDTO currentBankAccountDTO) throws CustomerNotFoundException {
        return bankAccountService.saveCurrentBankAccount(currentBankAccountDTO.getBalance(), currentBankAccountDTO.getOverDraft(), currentBankAccountDTO.getCustomerDTO().getId());
    }
 
    @PostMapping("/accounts/saving")
    public SavingBankAccountDTO saveSavingBankAccount(@RequestBody SavingBankAccountDTO savingBankAccountDTO) throws CustomerNotFoundException {
        return bankAccountService.saveSavingBankAccount(savingBankAccountDTO.getBalance(), savingBankAccountDTO.getInterestRate(), savingBankAccountDTO.getCustomerDTO().getId());
    }
}
