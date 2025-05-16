package ma.enset.digital_banking_system_backend.repositories;

import ma.enset.digital_banking_system_backend.entities.Customer;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

}
