package ma.enset.digital_banking_system_backend.repositories;

import ma.enset.digital_banking_system_backend.entities.AccountOperation;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountOperationRepository extends JpaRepository<AccountOperation, Long> {
  List<AccountOperation> findByBankAccountId(String accountId);

  Page<AccountOperation> findByBankAccountIdOrderByOperationDateDesc(String accountId, Pageable pageable);
}
