package br.com.filpo.billing;

import org.springframework.boot.SpringApplication;

public class TestBillingEngineApplication {

	public static void main(String[] args) {
		SpringApplication.from(BillingEngineApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
