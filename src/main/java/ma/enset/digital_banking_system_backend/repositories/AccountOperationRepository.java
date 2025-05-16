package ma.enset.digital_banking_system_backend.repositories;

import ma.enset.digital_banking_system_backend.entities.AccountOperation;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountOperationRepository extends JpaRepository<AccountOperation, Long> {

}
