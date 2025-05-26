package ma.enset.digital_banking_system_backend.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ma.enset.digital_banking_system_backend.enums.AccountStatus;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class CurrentBankAccountDTO extends BankAccountDTO {
    private String id;
    private double balance;
    private Date createdAt;
    private AccountStatus status;
    private CustomerDTO customerDTO;
    private double overDraft;
    private String createdBy;
}
