package ma.enset.digital_banking_system_backend;

import java.util.Date;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import ma.enset.digital_banking_system_backend.entities.CurrentAccount;
import ma.enset.digital_banking_system_backend.entities.Customer;
import ma.enset.digital_banking_system_backend.enums.AccountStatus;
import ma.enset.digital_banking_system_backend.repositories.*;

@SpringBootApplication
public class DigitalBankingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(DigitalBankingSystemApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(CustomerRepository customerRepository,
			BankAccountRepository bankAccountRepository,
			AccountOperationRepository accountOperationRepository) {
		return args -> {
			Stream.of("A", "B", "C").forEach(name -> {
				Customer customer = new Customer();
				customer.setName(name);
				customer.setEmail(name + "@gmail.com");
				customerRepository.save(customer);

			});
			customerRepository.findAll().forEach(customer -> {
				CurrentAccount currentAccount = new CurrentAccount();
				currentAccount.setId(UUID.randomUUID().toString());
				currentAccount.setBalance(Math.random() * 90000);
				currentAccount.setCreatedAt(new Date());
				currentAccount.setStatus(AccountStatus.CREATED);
				currentAccount.setCustomer(customer);
				currentAccount.setOverDraft(9000);
				bankAccountRepository.save(currentAccount);
			});
		

		};
	}

}
