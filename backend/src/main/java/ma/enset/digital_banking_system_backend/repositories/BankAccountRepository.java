package ma.enset.digital_banking_system_backend.repositories;

import ma.enset.digital_banking_system_backend.entities.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BankAccountRepository extends JpaRepository<BankAccount, String> {
    List<BankAccount> findByCustomerId(Long customerId);
}
