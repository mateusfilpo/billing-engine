package br.com.filpo.billing;

import br.com.filpo.billing.domain.model.Customer;
import br.com.filpo.billing.domain.port.out.SaveCustomerPort;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BillingEngineApplication {

	public static void main(String[] args) {
		SpringApplication.run(BillingEngineApplication.class, args);
	}

}
